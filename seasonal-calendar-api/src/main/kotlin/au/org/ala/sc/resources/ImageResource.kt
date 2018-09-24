package au.org.ala.sc.resources

import au.org.ala.sc.services.ImageService
import au.org.ala.sc.util.logger
import com.google.common.hash.Hashing
import com.google.common.hash.HashingInputStream
import io.dropwizard.jersey.caching.CacheControl
import org.apache.tika.Tika
import org.glassfish.jersey.media.multipart.BodyPartEntity
import org.glassfish.jersey.media.multipart.FormDataBodyPart
import org.glassfish.jersey.media.multipart.FormDataMultiPart
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Request
import javax.ws.rs.core.Response

@Path("images")
class ImageResource(private val baseDir: File, private val imageService: ImageService) {

    companion object {
        val log = logger()
        private val mediaTypeToExtension = mapOf(
            "image/jpeg" to "jpg",
            "image/png" to "png",
            "image/gif" to "gif",
            "image/bmp" to "bmp",
            "image/tiff" to "tiff",
            "image/webp" to "webp"
        )
        private val extensionToMediaType = mediaTypeToExtension.entries.associate { it.value to it.key }
        private val tika = Tika()
    }

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun upload(multiPart: FormDataMultiPart) : List<String> {
        val fields = multiPart.getFields("images")
        return fields.asSequence()
            .map { it to it.entity as? BodyPartEntity }
            .filter { it.second != null }
            .map{ (part, entity) -> saveFile(part, entity!!) }
            .toList()
    }

    @GET
    @Path("{id}")
    @CacheControl(immutable = true)
    @Produces("image/*")
    fun download(@PathParam("id") id: String, @QueryParam("w") width: Int?, @QueryParam("h") height: Int?, @Context request: Request) : Response {
        val file = File(baseDir, id)
        if (!file.canonicalFile.startsWith(baseDir.canonicalFile)) {
            throw WebApplicationException(400)
        }
        val resultFile = imageService.getImageFile(file, width, height)

        val lastModified = Date(resultFile.lastModified())
        val builder = request.evaluatePreconditions()
        return if (builder == null) {
            if (!resultFile.exists()) {
                throw WebApplicationException(404)
            }
            Response
                .ok(resultFile)
                .type(extensionToMediaType[resultFile.extension])
                .lastModified(lastModified)
                .header("Content-Disposition", "attachment; filename=${resultFile.name}")
                .build()
        } else
            builder.build()
    }

    private fun checkContentType(contentType: String) = mediaTypeToExtension.containsKey(contentType)

    private fun saveFile(data: FormDataBodyPart, entity: BodyPartEntity): String {
        val hashingInputStream = HashingInputStream(Hashing.murmur3_128(), entity.inputStream)

        val tempFile = Files.createTempFile("image", "")
        try {
            hashingInputStream.use { hashingIn ->
                tempFile.toFile().outputStream().use { out ->
                    hashingIn.copyTo(out)
                }
            }
            val contentType = tika.detect(tempFile)
            if (!checkContentType(contentType)) {
                throw IllegalStateException("Unknown content type $contentType")
            }
            val extension = mediaTypeToExtension[contentType]
            val id = hashingInputStream.hash().toString()
            log.debug("Uploaded file to {} with hash {}", tempFile, id)
            val dest = File(baseDir, "$id.$extension")
            if (!dest.exists()) {
                try {
                    Files.move(tempFile, dest.toPath())
                } catch (e: IOException) {
                    dest.delete()
                    throw e
                }
            }
            log.debug("Moved upload to destination {}", dest)
            return dest.name
        } finally {
            Files.deleteIfExists(tempFile)
            try {
                entity.cleanup()
            } catch (e: Exception) {
                log.error("Exception while cleaning up entity", e)
            }
        }
    }
}
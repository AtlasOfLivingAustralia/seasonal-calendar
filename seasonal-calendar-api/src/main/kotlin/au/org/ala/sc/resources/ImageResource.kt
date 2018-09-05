package au.org.ala.sc.resources

import com.google.common.hash.Hashing
import com.google.common.hash.HashingInputStream
import io.dropwizard.jersey.caching.CacheControl
import org.glassfish.jersey.media.multipart.BodyPartEntity
import org.glassfish.jersey.media.multipart.FormDataMultiPart
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Request
import javax.ws.rs.core.Response

@Path("images")
class ImageResource(val baseDir: File) {

    companion object {
        val log = LoggerFactory.getLogger(ImageResource::class.java)
    }

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun upload(multiPart: FormDataMultiPart) : List<String> {
        val fields = multiPart.getFields("images")
        return fields.map { it.entity as? BodyPartEntity }.filterNotNull().map(this::saveFile)
    }

    @GET
    @Path("{id}")
    @CacheControl(immutable = true)
    @Produces("image/jpg")
    fun download(@PathParam("id") id: String, @Context request: Request) : Response {
        val file = File(baseDir, "$id.jpg")
        if (!file.canonicalFile.startsWith(baseDir.canonicalFile)) {
            throw WebApplicationException(400)
        }
        val lastModified = Date(file.lastModified())
        val builder = request.evaluatePreconditions()
        if (builder == null)
            return Response.ok(file).lastModified(lastModified).build()
        else
            throw WebApplicationException(builder.build())
    }

    private fun saveFile(entity: BodyPartEntity): String {
        val hashingInputStream = HashingInputStream(Hashing.murmur3_128(), entity.inputStream)

        val tempFile = File.createTempFile("image", "jpg")
        try {
            hashingInputStream.use { hashingIn ->
                tempFile.outputStream().use { out ->
                    hashingIn.copyTo(out)
                }
            }
            val id = hashingInputStream.hash().toString()
            log.debug("Uploaded file to {} with hash {}", tempFile, id)
            val dest = File(baseDir, "$id.jpg")
            if (!dest.exists()) {
                try {
                    tempFile.copyTo(dest)
                } catch (e: IOException) {
                    dest.delete()
                    throw e
                }
            }
            log.debug("Moved upload to destination {}", dest)
            return id
        } finally {
            tempFile.delete()
            try {
                entity.cleanup()
            } catch (e: Exception) {
                log.error("Exception while cleaning up entity", e)
            }
        }
    }
}
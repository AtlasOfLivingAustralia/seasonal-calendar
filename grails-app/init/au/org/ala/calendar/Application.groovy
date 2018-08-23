package au.org.ala.calendar

import au.org.ala.profiles.service.ProfileServiceClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean

class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Bean
    Moshi moshi() {
        new Moshi.Builder().add(Date, new Rfc3339DateJsonAdapter().nullSafe()).build()
    }

    @Bean
    OkHttpClient okHttpClient() {
        new OkHttpClient.Builder().build()
    }

    @Bean
    ProfileServiceClient profileServiceClient() {
        new ProfileServiceClient.Builder(okHttpClient(), grailsApplication.config.getProperty('profile.service.baseUrl', String)).moshi(moshi()).build()
    }
}
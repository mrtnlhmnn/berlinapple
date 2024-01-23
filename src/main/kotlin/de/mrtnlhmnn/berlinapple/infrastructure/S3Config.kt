package de.mrtnlhmnn.berlinapple.infrastructure

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.StringUtils

@Configuration
open class S3Config {

    @Value("\${aws.s3.access_key:}")
    private val awsAccessKey: String? = null

    @Value("\${aws.s3.secret_key:}")
    private val awsSecretKey: String? = null

    @Value("\${aws.s3.region:eu-central-1}")
    private val s3Region: String? = null


    @Value("\${offline:false}")
    val offline = false

    @Value("\${aws.s3.bucketname}")
    val s3BucketName: String? = null

    @Value("\${aws.s3.movie.keyprefix}")
    val movieKeyPrefix: String? = "berlinapple2024/movies/movies-"

    @Bean
    open fun s3Client(): AmazonS3 {
        val amazonS3 = if (!StringUtils.hasLength(awsAccessKey) || !StringUtils.hasLength(awsSecretKey)) {
            AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.fromName(s3Region))
                    .build()
        } else {
            AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.fromName(s3Region))
                    .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(awsAccessKey, awsSecretKey)))
                    .build()
        }

        return amazonS3
    }
}

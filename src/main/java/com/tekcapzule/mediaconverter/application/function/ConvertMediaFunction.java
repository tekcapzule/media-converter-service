package com.tekcapzule.mediaconverter.application.function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.tekcapzule.mediaconverter.application.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.mediaconvert.*;
import software.amazon.awssdk.services.mediaconvert.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class ConvertMediaFunction implements Function<S3Event, String> {
    private static final String OUTPUT_BUCKET_NAME = "transcodedvideos.tekcapzule.com";
    private static final String OUTPUT_KEY_PREFIX = "transcoded/";

    private final AppConfig appConfig;

    public ConvertMediaFunction( final AppConfig appConfig) {
        this.appConfig = appConfig;
    }
   // @Override
//    public String handleRequest(S3Event input, Context context) {
//        try {
//            // Initialize AWS MediaConvert client
//            MediaConvertClient mediaConvertClient = MediaConvertClient.builder().build();
//
//            String bucketName = input.getRecords().get(0).getS3().getBucket().getName();
//            String objectKey = input.getRecords().get(0).getS3().getObject().getKey();
//            System.out.println("S3Event ObjectKey"+objectKey);
//
//            // Construct the input file URI for MediaConvert job
//            String inputFileUri = "s3://" + bucketName + "/" + objectKey;
//            // Create audio selector with default selection
//            AudioSelector audioSelector =  AudioSelector.builder().defaultSelection(AudioDefaultSelection.DEFAULT).build();
//            Map<String,AudioSelector> audios = new HashMap<>();
//            audios.put("Audio Selector 1",audioSelector);
//
//            // Create a job settings object
//            CreateJobResponse createJobResponse = mediaConvertClient.createJob(CreateJobRequest.builder()
//                    .role("arn:aws:iam::552781593359:role/service-role/MediaConvert_Default_Role")
//                    .settings(JobSettings.builder()
//                            .outputGroups(
//                                    createOutputGroup("MobileGroup", "mobile/", "system-Avc_16x9_480p_29_97fps_800kbps")
//                                    //  createOutputGroup("TabletGroup", "tablet/", "System-Avc_16x9_720p_29_97fps_2000kbps"),
//                                    //createOutputGroup("WebGroup", "web/", "System-Avc_16x9_720p_29_97fps_4000kbps")
//                            )
//                            .inputs(Input.builder().fileInput(inputFileUri).audioSelectors(audios).build())
//                            .build())
//                    .build());
//
//            // Wait for the job to complete
//            waitForJobCompletion(mediaConvertClient, createJobResponse.job().id());
//
//            // Cleanup resources
//            mediaConvertClient.cancelJob(CancelJobRequest.builder().build());
//
//            return "Transcoding job completed successfully.";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error in transcoding job: " + e.getMessage();
//        }
//    }

    private OutputGroup createOutputGroup(String groupName, String outputPrefix, String presetName) {
        return OutputGroup.builder()
                .name(groupName)
                .outputs(Output.builder()
                        .preset(presetName)
                        .containerSettings(ContainerSettings.builder()
                                .container(ContainerType.MP4)
                                .mp4Settings(Mp4Settings.builder().build())
                                .build())
                        .extension("mp4")
                        .build())
                .outputGroupSettings(OutputGroupSettings.builder().type(OutputGroupType.FILE_GROUP_SETTINGS)
                        .fileGroupSettings(FileGroupSettings.builder()
                                .destinationSettings(DestinationSettings.builder().s3Settings(S3DestinationSettings.builder().accessControl(S3DestinationAccessControl.builder().cannedAcl(S3ObjectCannedAcl.BUCKET_OWNER_FULL_CONTROL).build()).build()).build())
                                .destination("s3://" + OUTPUT_BUCKET_NAME + "/" + outputPrefix)
                                .build())
                        .build())
                .build();
    }

    private void waitForJobCompletion(MediaConvertClient mediaConvertClient, String jobId) throws InterruptedException {
        while (true) {
            GetJobResponse getJobResponse = mediaConvertClient.getJob(GetJobRequest.builder().id(jobId).build());
            JobStatus jobStatus = getJobResponse.job().status();

            if (jobStatus == JobStatus.SUBMITTED || jobStatus == JobStatus.PROGRESSING) {
                Thread.sleep(5000); // Sleep for 5 seconds before checking again
            } else if (jobStatus == JobStatus.COMPLETE) {
                break; // Job completed successfully
            } else {
                throw new RuntimeException("Error in transcoding job: " + jobStatus);
            }
        }
    }

    @Override
    public String apply(S3Event s3Event) {
        try {
            // Initialize AWS MediaConvert client
            MediaConvertClient mediaConvertClient = MediaConvertClient.builder().build();

            String bucketName = s3Event.getRecords().get(0).getS3().getBucket().getName();
            String objectKey = s3Event.getRecords().get(0).getS3().getObject().getKey();
            System.out.println("S3Event ObjectKey"+objectKey);

            // Construct the input file URI for MediaConvert job
            String inputFileUri = "s3://" + bucketName + "/" + objectKey;
            // Create audio selector with default selection
            AudioSelector audioSelector =  AudioSelector.builder().defaultSelection(AudioDefaultSelection.DEFAULT).build();
            Map<String,AudioSelector> audios = new HashMap<>();
            audios.put("Audio Selector 1",audioSelector);

            // Create a job settings object
            CreateJobResponse createJobResponse = mediaConvertClient.createJob(CreateJobRequest.builder()
                    .role("arn:aws:iam::552781593359:role/service-role/MediaConvert_Default_Role")
                    .settings(JobSettings.builder()
                            .outputGroups(
                                    createOutputGroup("MobileGroup", "mobile/", "system-Avc_16x9_480p_29_97fps_800kbps"),
                                      createOutputGroup("TabletGroup", "tablet/", "system-Avc_16x9_720p_29_97fps_2000kbps"),
                                    createOutputGroup("WebGroup", "web/", "system-Avc_16x9_720p_29_97fps_4000kbps")
                            )
                            .inputs(Input.builder().fileInput(inputFileUri).audioSelectors(audios).build())
                            .build())
                    .build());

            // Wait for the job to complete
            waitForJobCompletion(mediaConvertClient, createJobResponse.job().id());

            // Cleanup resources
            mediaConvertClient.cancelJob(CancelJobRequest.builder().build());

            return "Transcoding job completed successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in transcoding job: " + e.getMessage();
        }
    }
}
package com.ege.wooda.global.s3;

/**
 * @author          locmment
 * @record          S3File
 * @description     aws S3 파일 관련 dto
 */

public record S3File(String fileName, String fileUrl) {

    public S3File { }
}
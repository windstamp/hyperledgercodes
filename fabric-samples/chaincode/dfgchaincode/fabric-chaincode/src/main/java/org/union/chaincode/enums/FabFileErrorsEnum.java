package org.union.chaincode.enums;

/**
 * @description 文件链码错误枚举
 * @date
 */
public enum FabFileErrorsEnum {
    /**
     * 文件不存在
     */
    FILE_NOT_FOUND,
    /**
     * 文件已存在
     */
    FILE_ALREADY_EXISTS,
    /**
     * 长度过长
     */
    LENGTH_TOO_LARGE,
    /**
     * 文件解析异常
     */
    JSON_DESERIALIZATIONFEATURE_ERROR
}

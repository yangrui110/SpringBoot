package com.yangframe.chat.entity;

import lombok.Data;

/**
 * @autor 杨瑞
 * @date 2019/9/26 19:27
 */
@Data
public class HistoryEntity {
    private String userLoginId;
    private String receiveId;
    private String receivedType;
    private Integer start;
    private Integer end;
}

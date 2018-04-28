package com.yingu.project.api;

import lombok.*;

/**
 * Created by MMM on 2018/03/21.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBase {
    public Status status;
    public String message;
}

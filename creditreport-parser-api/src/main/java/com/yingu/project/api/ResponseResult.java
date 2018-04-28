package com.yingu.project.api;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class ResponseResult<T> extends ResponseBase{
    public List<T> data = new ArrayList();

}

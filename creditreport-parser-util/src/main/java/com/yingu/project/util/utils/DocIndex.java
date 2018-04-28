package com.yingu.project.util.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocIndex {
    int begin;
    int end;

    public DocIndex(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }
}

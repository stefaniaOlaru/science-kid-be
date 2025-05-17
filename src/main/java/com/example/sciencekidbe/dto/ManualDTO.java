package com.example.sciencekidbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManualDTO {
    private Long manualId;
    private String manualName;
    private String publisher;
    private List<String> classNames;
}

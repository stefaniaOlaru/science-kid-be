package com.example.sciencekidbe.helpers;

import com.example.sciencekidbe.enums.ContainerName;

public class FileHelpers {

    // Other helper methods...

    public static ContainerName mapStringToContainerName(String containerName) {
        // Normalize input: lowercase + replace "-" with "_"
        String normalized = containerName.toLowerCase().replace("-", "_");

        for (ContainerName cn : ContainerName.values()) {
            if (cn.name().toLowerCase().equals(normalized)) {
                return cn;
            }
        }
        throw new IllegalArgumentException("No enum constant for container name: " + containerName);
    }

    public static String getBlobContainerName(ContainerName containerName) {
        if (containerName == ContainerName.PROFESSOR_CV) {
            return "professor-cv";  // special case
        } else if (containerName == ContainerName.LECTURE_VIDEO) {
            return "lecture-video";
        } else if (containerName == ContainerName.ACTUAL_VIDEO) {
            return "actual-video";
        }

        return containerName.name().toLowerCase(); // default behavior
    }
}

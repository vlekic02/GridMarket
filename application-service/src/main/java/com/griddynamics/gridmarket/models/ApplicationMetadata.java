package com.griddynamics.gridmarket.models;

import com.griddynamics.gridmarket.http.request.ApplicationUploadRequest;

public record ApplicationMetadata(ApplicationUploadRequest request, long publisherId) {

}

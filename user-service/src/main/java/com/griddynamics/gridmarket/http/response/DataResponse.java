package com.griddynamics.gridmarket.http.response;

public class DataResponse<T> {

  private final T data;

  private DataResponse(T data) {
    this.data = data;
  }

  public static <T> DataResponse<T> of(T data) {
    return new DataResponse<>(data);
  }

  public T getData() {
    return data;
  }
}
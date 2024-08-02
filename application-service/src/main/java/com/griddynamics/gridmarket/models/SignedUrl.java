package com.griddynamics.gridmarket.models;

import com.griddynamics.jacksonjsonapi.models.Resource;

public class SignedUrl extends Resource {

  private final String url;

  public SignedUrl(long id, String url) {
    super(id, "signed_url");
    this.url = url;
  }

  public String getUrl() {
    return url;
  }
}

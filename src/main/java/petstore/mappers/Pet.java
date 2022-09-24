package petstore.mappers;

import java.util.ArrayList;
import java.util.List;

public class Pet {
  long id;
  String name = null;
  Status status = null;

  Category category = null;
  List<String> photoUrls = new ArrayList<>();
  List<Tag> tags = new ArrayList<>();

  //
  // ***************************  Mapper methods
  //
  public long getId() {
    return id;
  }

  public Pet setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public Pet setName(String name) {
    this.name = name;
    return this;
  }

  public Status getStatus() {

    return status;
  }

  public Pet setStatus(String status) {
    this.status = Status.valueOf(status);
    return this;
  }

  public Pet setStatus(Status status) {
    this.status = status;
    return this;
  }

  public Category getCategory() {
    return category;
  }

  public Pet setCategory(Category category) {
    this.category = category;
    return this;
  }

  public List<String> getPhotoUrls() {
    return photoUrls;
  }

  public Pet setPhotoUrls(List<String> photoUrls) {
    this.photoUrls = photoUrls;
    return this;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public Pet setTags(List<Tag> tags) {
    this.tags = tags;
    return this;
  }

  //
  // ***************************  Utils for single adding during tests
  //

  public Pet addTag(Tag tag) {
    this.tags.add(tag);
    return this;
  }

  public Pet addPhotoUrl(String photoUrl) {
    this.photoUrls.add(photoUrl);
    return this;
  }
}

package com.Ganeshkumarkvt.kvthub;
class PostData {
    String Time;
    String ImageName;
    String ImageLink;
    String FromWhom;
    String Description;

    public PostData() {
    }


    @Override
    public String toString() {
        return "PostData{" +
                "Time='" + Time + '\'' +
                ", ImageName='" + ImageName + '\'' +
                ", ImageLink='" + ImageLink + '\'' +
                ", FromWhom='" + FromWhom + '\'' +
                ", Description='" + Description + '\'' +
                '}';
    }


    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
       this.Time = Time;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String ImageName) {
        this.ImageName = ImageName;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public void setImageLink(String ImageLink) {
        this.ImageLink = ImageLink;
    }

    public String getFromWhom() {
        return FromWhom;
    }

    public void setFromWhom(String FromWhom) {
        this.FromWhom = FromWhom;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }
}

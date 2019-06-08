public class VideoUri {

    private String uriOfOpenVideo;
    private String videoName;
    private int number;

    public VideoUri (){
        uriOfOpenVideo = new String();
        videoName = new String();
        number=0;
    }

//    public VideoUri (String uri,String videoName){
//        this.uriOfOpenVideo= uri;
//        this.videoName= videoName;
//    }

    public String getUri() {
        return uriOfOpenVideo;
    }

    public void setUriOfOpenVideo (String uriOfOpenVideo) {
        this.uriOfOpenVideo=uriOfOpenVideo;
    }

    public String getVideoName () {return  videoName;}

    public  void setVideoName (String videoName){this.videoName=videoName;}

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}

package com.bklifetw.liang;

public class Post {
    public String  Latitude;
    public String  Longitude;
    public String  Ticketinfo;
    public String  Opentime;
    public String  Picdescribe1;
    public String Tel;
    public String Name;
    public String posterThumbnailUrl;
    public String Add;
    public String Content;
    public String Zipcode;

    public String S_PlaceDes;
    public String Bike_length;
    public String Toldescribe;



    public Post( String Name,
                 String S_PlaceDes, String Bike_length, String Toldescribe,String Add) {

        this.Name = Name;  //名稱
        this.S_PlaceDes = S_PlaceDes; //鄉鎮
        this.Bike_length = Bike_length;  //距離
        this.Toldescribe = Toldescribe; //描述
        this.Add = Add;  //住址

    }

    public Post(String Name, String posterThumbnailUrl, String Add, String content,
                String Zipcode, String latitude, String longitude,
                String ticketinfo, String opentime, String picdescribe1, String tel) {
        this.Name = Name;  //名稱
        this.posterThumbnailUrl = posterThumbnailUrl; //圖片
        this.Add = Add;  //住址
        this.Content = content; //描述
        this.Zipcode = Zipcode; //郵遞區號
        this.Latitude =latitude;//緯度
        this.Longitude =longitude;//經度
        this.Ticketinfo =ticketinfo;//票價訊息
        this.Opentime =opentime;//開放時間
        this.Picdescribe1 =picdescribe1;//圖片說明
        this.Tel =tel;//電話


    }
}
package com.gaiay.support.umeng;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.umeng.socialize.media.UMImage;

import java.util.Map;

/**
 * Created by zhouchao on 2016/1/20.
 */
public class ModelShare implements Parcelable {
    /**
     * 分享内容的title
     */
    public String title;
    /**
     * 分享内容的描述信息
     */
    public String description;
    /**
     * 概要，在微信或者QQ手机分享时，需要同时显示标题和概要时使用
     */
    public String summary;
    /**
     * 微信朋友圈分享内容
     */
    public String weixinCircle;
    /**
     * 分享内容的url地址，比如音频地址
     */
    public String mediaUrl;
    /**
     * 分享内容的web页面
     */
    public String webUrl;
    /**
     * 分享的图片
     */
    public Bitmap img;
    /**
     * 分享的图片
     */
    public UMImage imgUM;
    /**
     * 分享的图片URL
     */
    public String imgUrl;
    /**
     * 分享类型：3 音频，4 视频
     */
    public int type;
    /**
     * 短链接
     */
    public String shortUrl;
    /**
     * 额外的属性
     */
    public Map extras;
    /**
     * 掌信的标题
     */
    public String chatTitle;
    /**
     * 掌信的内容
     */
    public String chatContent;
    /**
     * 消息类型
     */
    public int chatContentType;
    public String shareUrl;
    public String innerUrl;
    /**
     * 能量卡，话题的id
     */
    public String id;
    public String bizId;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(summary);
        dest.writeString(weixinCircle);
        dest.writeString(mediaUrl);
        dest.writeString(webUrl);

        dest.writeInt(type);
        dest.writeString(imgUrl);
        dest.writeString(chatTitle);
        dest.writeString(chatContent);
        dest.writeString(shareUrl);
        dest.writeString(innerUrl);
        dest.writeString(id);
        dest.writeInt(chatContentType);
        dest.writeString(bizId);
    }

    public static final Parcelable.Creator<ModelShare> CREATOR = new Parcelable.Creator<ModelShare>() {
        @Override
        public ModelShare createFromParcel(Parcel source) {
            ModelShare model = new ModelShare();
            model.title = source.readString();
            model.description = source.readString();
            model.summary = source.readString();
            model.weixinCircle = source.readString();
            model.mediaUrl = source.readString();
            model.webUrl = source.readString();

            model.type = source.readInt();
            model.imgUrl = source.readString();
            model.chatTitle = source.readString();
            model.chatContent = source.readString();
            model.shareUrl = source.readString();
            model.innerUrl = source.readString();
            model.id = source.readString();
            model.chatContentType = source.readInt();
            model.bizId = source.readString();
            return model;
        }

        @Override
        public ModelShare[] newArray(int size) {
            return new ModelShare[size];
        }
    };
}

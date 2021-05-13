package com.xupt.util;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoSimilarity implements Comparable<VideoSimilarity>, Serializable {
    private long videoId; //videoID
    private Double similarity; //similarity

    public VideoSimilarity() {
        this.videoId = -1;
        this.similarity = 0d;
    }
    public VideoSimilarity(long videoId, Double similarity) {
        this.videoId = videoId;
        this.similarity = similarity;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof VideoSimilarity))
            return false;
        if (obj == this)
            return true;

        //  double类型的数据不应该直接比较
        return this.videoId == ((VideoSimilarity) obj).videoId && this.similarity == ((VideoSimilarity) obj).similarity;
    }

    public int hashCode(){
        return (int)(videoId + similarity);
    }


    @Override
    public String toString() {
        return "id:" + videoId + ",similarity:" + similarity;
    }

    @Override
    public int compareTo(VideoSimilarity obj) {
        if(this.similarity > obj.similarity) {
            return 1;
        } else if(this.similarity < obj.similarity) {
            return -1;
        }
        return 0;
    }
}


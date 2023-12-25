package com.example.vietnamesehtr;

import java.util.List;

public class ImageRes {
    private List<String> result;

    public ImageRes() {
    }

    public ImageRes(List<String> result) {
        this.result = result;
    }

    public List<String> getresult() {
        return result;
    }

    public void setresult(List<String> result) {
        this.result = result;
    }
    public String toString(){
        String res = "";
        for (String word: result){
            res+=word+" ";
        }
        return res;
    }
}

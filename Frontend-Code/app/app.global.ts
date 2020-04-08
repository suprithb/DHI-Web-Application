import { Injectable } from "@angular/core";


@Injectable({
providedIn: 'root'
})
export class AppGlobals { 
    chat=[];
    favImages=[];
    clickedimage:any;
    getChat(){
        return this.chat;
    }
    setChat(chat){
this.chat = chat;
    }
    getFavImages(){
        return this.favImages;
    }
  
            
    getUrl(){
        return this.clickedimage;
    }
    setUrl(url){
    this.clickedimage = url;
    }
}
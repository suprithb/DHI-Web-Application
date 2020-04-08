import { Injectable } from '@angular/core';
import { Observable, Subject } from "rxjs/Rx";
import { WebsocketService } from "./websocket.service";

const CHAT_URL = "ws"+ '://' + "127.0.0.1:8000" + "/chat" + "/shrill";

export interface Message {
message: string;
handle:string;
}

@Injectable({
providedIn: 'root'
})
export class ChatService {
public messages: Subject<Message>;

constructor(wsService: WebsocketService) {
this.messages = <Subject<Message>>wsService.connect(CHAT_URL).map(
(response: MessageEvent): Message => {
let data = JSON.parse(response.data);
return {
message: data.reply,
handle:""
};
}
);
}
} 
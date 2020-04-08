import { Component } from '@angular/core';
import { CommonService } from '../services/common.service';
import { HttpClient } from '@angular/common/http'; 
import { Observable } from 'rxjs/Observable';
// import config from './assets/Json/configuration.json'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'data-talk';

  constructor(public common:CommonService,public http:HttpClient){
  }
  ngOnInit(){
    this.http.get("assets/Json/configuration.json").subscribe(data=>{
      this.common.domain=data['domain'];
      this.common.OcpApimTrace=data['Ocp-Apim-Trace'];
      this.common.OcpApimSubscriptionKey=data['Ocp-Apim-Subscription-Key'];
    })
    // this.common.domain=config.domain;
  }
  
}

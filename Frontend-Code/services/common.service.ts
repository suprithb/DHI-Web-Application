import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders,HttpParams} from '@angular/common/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material';

@Injectable({
  providedIn: 'root'
})
export class CommonService {
displaySidebar:boolean=false;
connectActive:boolean=false;
discoverActive:boolean=false;
storiesActive:boolean=false;
craftActive:boolean=false;
roomid:any=12345;
queryResponse:any=[];
firstName:any;
uploadedFile:any;
username:any;
reqText:any;
selectedIndex:number=2;
loading:boolean=false;
serverResponse:boolean=false;
serverTextMessage:string="";
isStoryOpen:Boolean=false;
openedStoryTitle:String="";
openedStory:any=[];
cardsArray:any=[];
displayOptions:boolean=false;
storyRefreshDate:string="";
cardRefreshDate:string="";
errorResponse:boolean=false;
successResponse:boolean=false;
displaySidebarStyle='100%';
graphColor='';
rememberMe:boolean=false;
brushExtent:number=5;
SIUnit=[];
// craftStoriesArray=[];
// domain='http://ipsolution-v2.eastus.cloudapp.azure.com';
domain='';
OcpApimTrace='';
OcpApimSubscriptionKey='';

public discoverTableData = new BehaviorSubject([])
openSnackBar(message: string, action: string) {
  let config = new MatSnackBarConfig();
  config.duration = 6000;
  config.panelClass = ['red-snackbar']
  this.snackBar.open(message, action, config);
}
openGreenSnackBar(message: string, action: string) {
  let config = new MatSnackBarConfig();
  config.duration = 6000;
  config.panelClass = ['green-snackbar']
  this.snackBar.open(message, action, config);
}
  constructor(private httpClient: HttpClient, public snackBar: MatSnackBar ) { }
  response_from_bot(text){
    let clientText={
      clientTextMessage:text
    }
    let config_headers = new HttpHeaders({
      'Ocp-Apim-Trace':this.OcpApimTrace,
      'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
    });
    let config_options = { headers:config_headers };
    return this.httpClient.post(this.domain+"/DHI/v2/query/"+this.roomid,clientText,config_options);
}
login(userObj){
  let config_headers = new HttpHeaders({
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let config_options = { headers:config_headers };
  return this.httpClient.post(this.domain+"/DHI/api/auth/login",userObj,config_options);
}
fileUpload(file){
  let headers = new HttpHeaders({
    'user-name' : this.username,
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let options = { headers: headers };
  return this.httpClient.post(this.domain+'/DHI/api/upload/csv',file,options)
}
getStories(){
  let headers = new HttpHeaders({
    'user-name' : this.username,
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let options = { headers: headers };
return this.httpClient.get(this.domain+"/DHI/api/stories",options);
} 

getRecentStories(offset,order){
  let headers = new HttpHeaders({
    'user-name' : this.username,
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let options = { headers: headers};
return this.httpClient.get(this.domain+"/DHI/api/stories?offset="+offset+"&order="+order,options);
}
createPPT(title){
  let headers = new HttpHeaders({
    'user-name' : this.username,
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let options = { headers: headers};
return this.httpClient.get(this.domain+"/DHI/api/stories/ppt/"+title,options);
}
downloadUrl(url){
  let headers = new HttpHeaders({
    'user-name' : this.username,
    'Accept':'application/octet-stream',
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
return this.httpClient.get(url,{ headers: headers, responseType: 'blob'});

}
addStory(story){
  let headers = new HttpHeaders({
    'user-name' : this.username,
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let options = { headers: headers };
  console.log(story);
return this.httpClient.post(this.domain+"/DHI/api/stories",story,options);
}
autoSuggestion(){
  let headers = new HttpHeaders({
    'user-name' : this.username,
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let options = { headers: headers};
return this.httpClient.get(this.domain+"/DHI/api/auto-suggestions",options);

}
deleteStory(story){
  let headers = new HttpHeaders({
    'user-name' : this.username,
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let options = { headers: headers};
  return this.httpClient.delete(this.domain+"/DHI/api/stories/"+story.storyTitle,options)
   
}
saveDiscoverData(dataObj){
  let headers = new HttpHeaders({
    'user-name' : this.username,
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let options = { headers: headers };
  return this.httpClient.post(this.domain+"/DHI/api/discovery",dataObj,options)
}
// formatTableDataPromise(tableData){
//   return new Promise(async (resolve,rej)=>{
//     let tableObj={
//       "tabularData": tableData
//     }
//     let config_headers = new HttpHeaders({
//       'Ocp-Apim-Trace':this.OcpApimTrace,
//       'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
//     });
//     let config_options = { headers:config_headers };
//     let data = await this.httpClient.post(this.domain+"/DHI/api/data-formatting/query-data",tableObj,config_options);
//     data.subscribe((res)=>{
//       resolve(res)
//     })
    
//   });
 

// }

formatTableData(tableData){
  let tableObj={
    "tabularData": tableData
  }
  let config_headers = new HttpHeaders({
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let config_options = { headers:config_headers };
  return this.httpClient.post(this.domain+"/DHI/api/data-formatting/query-data",tableObj,config_options)

}
getFavoriteData(){
  let headers = new HttpHeaders({
    'user-name' : this.username,
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let options = { headers: headers };
  return this.httpClient.get(this.domain+"/DHI/api/favourite",options)
}
getDiscoverData(){
  let headers = new HttpHeaders({
    'user-name' : this.username,
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let options = { headers: headers };
return this.httpClient.get(this.domain+"/DHI/api/discovery?data-file-name="+this.uploadedFile,options)
}

//To get font-family from API
getFonts(){
  let config_headers = new HttpHeaders({
    'Ocp-Apim-Trace':this.OcpApimTrace,
    'Ocp-Apim-Subscription-Key':this.OcpApimSubscriptionKey
  });
  let config_options = { headers:config_headers };
return this.httpClient.get(this.domain+"/DHI/assets/fonts",config_options);
}

hideSidebar(){
  
  this.displaySidebarStyle ='83.33333333%'
  this.displaySidebar = true;
  setTimeout(()=>{
    this.displaySidebar = false;
}, 6000);
  setTimeout(()=>{
    this.displaySidebarStyle = '100%'
}, 7000);
}
showSidebar(){
  this.displaySidebar=true;
  this.displaySidebarStyle ='83.33333333%';
}
}

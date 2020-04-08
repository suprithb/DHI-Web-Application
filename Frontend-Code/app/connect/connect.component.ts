import { Component, OnInit } from '@angular/core';
import { CommonService } from '../../services/common.service';
import { RecentStoriesComponent } from '../recent-stories/recent-stories.component';
import {Router} from '@angular/router';
@Component({
  selector: 'connect',
  templateUrl: './connect.component.html',
  styleUrls: ['./connect.component.css']
})
export class ConnectComponent implements OnInit {
selected:boolean=false;
indexNo:any;

constructor(public commonService:CommonService,public router:Router) { 
  this.commonService.serverResponse=false;
  this.commonService.serverTextMessage="";
  }
  upload(index) {
    this.selected=true;
    this.indexNo=index;
    document.getElementById('fileToUpload').click();
   }
   uploadFileToServe(e){
    this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
    const formData = new FormData();
    let i;
    for(i=0;i<e.target.files.length;i++)
    formData.append('file',e.target.files[i]);
    // formData.append('file', e.target.files['0']);
    this.commonService.uploadedFile=e.target.files[0].name;
    this.commonService.loading=true;
    this.commonService.fileUpload(formData).subscribe((response)=>{
      if(response['status']=="success")
      {
        this.commonService.loading=false;
        this.commonService.successResponse=true;
        this.commonService.serverTextMessage="File uploaded successfully"
        this.commonService.getDiscoverData().subscribe((response)=>{
          if(response['status']=="success"){
            this.commonService.discoverTableData.next(response['discovery']);
            this.commonService.connectActive=false;
            this.commonService.discoverActive=true;
            this.router.navigateByUrl('/discover');
          }
          else {
            this.commonService.loading=false;
            this.commonService.errorResponse=true;
            this.commonService.serverTextMessage=response['description'];
          }
        },
      error=>{
        this.commonService.loading=false;
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=error['error']['description'];
      })
      }
    },
      error=>{
        this.commonService.loading=false;
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=error['message'];
});
e.currentTarget.value="";
   }
  ngOnInit() {
    this.commonService.connectActive=true;
  }
  ngOnDestroy(){
    this.commonService.connectActive=false;
  }

}

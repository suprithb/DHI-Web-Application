import { Component, OnInit } from '@angular/core';
import { CommonService } from '../../services/common.service';
import {MAT_DIALOG_DATA} from '@angular/material';
import { Inject } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-craft-story-dialog',
  templateUrl: './craft-story-dialog.component.html',
  styleUrls: ['./craft-story-dialog.component.css']
})
export class CraftStoryDialogComponent implements OnInit {
  constructor(public common:CommonService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public router:Router) { }

  ngOnInit() {
  }
saveStory(){
  this.common.serverResponse=this.common.errorResponse=this.common.successResponse=false;
  let storyObj={
    "storyTitle":this.data.storyName,
    "storyItem":this.common.queryResponse
  }
  // console.log(storyObj.)
  this.common.addStory(storyObj).subscribe((response)=>{
    if(response['status']=="Success")
    {
      this.router.navigateByUrl('/stories');
    }
    else {
      this.common.errorResponse=true;
      this.common.serverTextMessage=response['description'];
    }
  },
    error=>{
      this.common.errorResponse=true;
      this.common.serverTextMessage=error['message'];
});
}
}

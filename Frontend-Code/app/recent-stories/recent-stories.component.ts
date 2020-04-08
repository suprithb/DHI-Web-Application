import { Component, OnInit } from '@angular/core';
import { CommonService } from '../../services/common.service';
import * as moment from 'moment';
import { ChartsService } from '../../services/charts.service';
@Component({
  selector: 'recent-stories',
  templateUrl: './recent-stories.component.html',
  styleUrls: ['./recent-stories.component.css']
})
export class RecentStoriesComponent implements OnInit {
  public recentStories: any = [];
  titleClass:string;
  storyNameClass:string;
  storySubclass:string;
  public showRecentStories:boolean=false;
  constructor(public commonService:CommonService,public charts:ChartsService) {
    this.titleClass="col-lg-2 col-md-2 col-sm-2 col-xs-2 text-div"
          this.storyNameClass="col-lg-10 col-md-10 col-sm-10 col-xs-10"
          this.storySubclass="col-lg-4 col-md-4 col-sm-4 col-xs-4 storyname"
   }

  ngOnInit() {
  this.getRecentStories(3,"asc");
  }
  getRecentStories(offset,order){
    this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
    this.commonService.getRecentStories(offset,order).subscribe((response)=>{
      if(response)
      {
        this.recentStories=response['stories'];
      }
      else if (response['status']=="error"){
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=response['description'];
      }
    },
      error=>{
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=error['error'];
});
  }
openStory(story){
  this.commonService.openedStory=story.storyItem;
  this.commonService.cardsArray=[];
  this.commonService.cardsArray=this.commonService.openedStory;
  this.commonService.storiesActive=false;
  this.commonService.craftActive=true;
  this.commonService.isStoryOpen=true;
  this.commonService.openedStoryTitle=story.storyTitle;
  this.commonService.cardRefreshDate=moment(parseInt(story.lastUpdatedTimestamp)).format("DD MMM YYYY");
  setTimeout(()=>{
    this.charts.reRenderCharts(story.storyItem);
  },200)
   }
}

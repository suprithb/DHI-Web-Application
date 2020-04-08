import { Component, OnInit, ViewChild } from '@angular/core';
import {Router} from '@angular/router';
import { CommonService } from '../../services/common.service';
import { formatDate } from '@angular/common';
import * as moment from 'moment';
import { ChartsService } from '../../services/charts.service';
import { _ } from 'underscore';

@Component({
  selector: 'app-stories',
  templateUrl: './stories.component.html',
  styleUrls: ['./stories.component.css']
})
export class StoriesComponent implements OnInit {
stories:any[]=[]
indexNo:any;
displayOptionsIcon:Boolean=false;
  constructor(private router: Router,
    public commonService:CommonService,
    public charts:ChartsService) {
  }
  ngOnInit() {
    this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
    this.commonService.storiesActive=true;
    this.commonService.loading=true;
    // this.commonService.craftStoriesArray=this.commonService.cardsArray;
    this.commonService.cardsArray=[];
this.getStories();
  }
  format(v,unit,index){
          
    switch(unit){
        case 'millions':this.commonService.SIUnit[index]='M';
                        return Math.ceil((parseInt(v)/1000000));
                        

        case 'thousands':this.commonService.SIUnit[index]='K';
                         return Math.ceil((parseInt(v)/1000));

        default:
                this.commonService.SIUnit[index]='';
                return parseInt(v);
    }
}
 renderStoryGraph(tabularData,columnNames,sortObj,unit,index){
  //  let response =  await this.commonService.formatTableDataPromise(tabularData)
  //     if(response)
  //     {
  //       Object.values(response).map(ele=>{
  //         if(ele[columnNames[1]]!=undefined)
  //         ele[columnNames[1]]=this.format(parseInt(ele[columnNames[1]].replace(/,/g, "")),unit,index);
  //       })


  //       if(sortObj && sortObj.direction=='asc')
  //       return(_.sortBy(response,sortObj.active));
  //       else if( sortObj && sortObj.direction=='desc')
  //       return(_.sortBy(response,sortObj.active).reverse());
  //       else
  //       return(Object.values(response));

  //     }
  //     else if (response['status']=="error"){
  //     }
let tableData=[];
tabularData.row.forEach((element) => {
      let JsonObj;
          for(let i=0;i<element.columnValues.length;i++)
          {
              if(JsonObj==undefined)
              JsonObj={[element.columnValues[0].columnName]:element.columnValues[0].columnValue}
              else
              JsonObj[element.columnValues[i].columnName]=element.columnValues[i].columnValue;
          }
          tableData.push(JsonObj);
          
  });
  Object.values(tableData).map(ele=>{
            if(ele[columnNames[1]]!=undefined)
            ele[columnNames[1]]=this.format(parseInt(ele[columnNames[1]].replace(/,/g, "")),unit,index);
          })

        if(sortObj && sortObj.direction=='asc')
        return(_.sortBy(tableData,sortObj.active));
        else if( sortObj && sortObj.direction=='desc')
        return(_.sortBy(tableData,sortObj.active).reverse());
        else
        return(Object.values(tableData));
  }
  Refresh(){
    this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
    let month = formatDate(new Date(), 'MM', 'en');
    let date = moment(month, 'MM').format('MMMM') + " " + formatDate(new Date(), 'dd yyyy', 'en');
    let time = moment().format('hh:mm A');
    this.commonService.storyRefreshDate=date+" "+time;

    this.commonService.loading=true;
    this.getStories();
  }
  getStories(){
    this.commonService.getStories().subscribe(response=>{
      if(response)
      {
        this.stories=[];
        this.stories=response['stories'];
        this.commonService.loading=false;
        setTimeout(()=>{
          for(let i=0;i<this.stories.length;i++){
            this.commonService.cardsArray=[];
            for(let j=0;j<this.stories[i].storyItem.length;j++){
              if(j<4){
                let cardsArrayItem = {
                  "title":this.stories[i].storyItem[j]['title'],
                  "userQueryMessage":this.stories[i].storyItem[j]['userQueryMessage'],
                  "tabularData":this.stories[i].storyItem[j]['tabularData'],
                  "color":this.stories[i].storyItem[j]['color'],
                  "fontFamily":this.stories[i].storyItem[j]['font-family'],
                  "unit":this.stories[i].storyItem[j]['unit'],
                  "graphType":this.stories[i].storyItem[j]['graphType'],
                  "graphTypes":this.stories[i].storyItem[j]['tabularData']['graphTypes'],
                  "columnWithTypes":this.stories[i].storyItem[j]['tabularData']['columnWithTypes'],
                  "fontSize":this.stories[i].storyItem[j]['font-size'],
                  "sortObj":this.stories[i].storyItem[j]['sortObj'],
                  "sortedData":this.renderStoryGraph(this.stories[i].storyItem[j]['tabularData'],this.stories[i].storyItem[j]['tabularData']['columNames'],this.stories[i].storyItem[j]['sortObj'],this.stories[i].storyItem[j]['unit'],j),
                  "columNames":this.stories[i].storyItem[j]['tabularData']['columNames'],
                  "storyIndex":i,
                  "storyItemIndex":j
                };
                this.commonService.cardsArray.push(cardsArrayItem);
                this.charts.findGraph(cardsArrayItem,cardsArrayItem.storyItemIndex);
              }
            }
          }
        },200)

        if(this.stories.length==0)
        {
          this.commonService.serverResponse=true;
          this.commonService.serverTextMessage="No stories found";
          // this.commonService.openGreySnackBar(this.commonService.serverTextMessage,"");
        }
      }
      else if (response['status']=="error"){
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=response['description'];
      }
    },
      error=>{
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=error['error']['description'];
});
  }
  openStory(story){
    this.commonService.openedStory=story.storyItem;
    // this.commonService.cardsArray=this.commonService.openedStory;
    this.commonService.storiesActive=false;
    this.commonService.craftActive=true;
    this.commonService.isStoryOpen=true;
    this.commonService.openedStoryTitle=story.storyTitle;
    this.commonService.cardRefreshDate=moment(parseInt(story.lastUpdatedTimestamp)).format("DD MMM YYYY");
    this.router.navigateByUrl('/craft-stories')
  }
  changeStyle($event,i){
    $event.preventDefault();
    this.indexNo=i;
    this.displayOptionsIcon=$event.type=='mouseover'?true:false;
    }
    deleteStory(story){
      this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
      this.commonService.deleteStory(story).subscribe((response)=>{
        if(response){
          this.Refresh();
          this.commonService.successResponse=true;
          this.commonService.serverTextMessage=response['description'];
        }
      },
      error=>{
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=error['error']['description'];
})
        }
ngOnDestroy(){
  this.commonService.storiesActive=false;
}
}


import { Component, OnInit,ViewChild ,AfterViewInit} from '@angular/core';
import { CommonService } from '../../services/common.service';
import { ChartsService } from '../../services/charts.service';
import { SpeechRecognitionService } from "../../services/speech-recognition.service";
import { formatDate } from '@angular/common';
import * as moment from 'moment';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import {MatAutocompleteTrigger} from '@angular/material';

interface IWindow extends Window {
  webkitAudioContext: any;
  Recorder: any;
}
@Component({
  selector: 'header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
@ViewChild(MatAutocompleteTrigger) trigger: MatAutocompleteTrigger;
@ViewChild('input') input: any;
@ViewChild('body') body: any;
query:string="";
recording: Boolean;
res:Boolean=false;
showSearchButton: boolean;
speechData: string;
chats = [];
image_path = "";
tabularContent:any;
noImage: boolean=false;
isMessage:Boolean=false;
options:boolean;
table:Boolean=false;
message:string;
myControl = new FormControl();
autocomplete_options: string[] = [];
filteredOptions: Observable<string[]>;
option_selected:boolean=false;
isSelected:boolean = false;
typedValue = '';
keyCode:number=0;
audio:boolean=true;

  constructor(public speechRecognitionService: SpeechRecognitionService,
    public common:CommonService,
    public chart:ChartsService
  ) { 
  }

  ngOnInit() {
    this.filterOptionsInitialization()
  }

  empty(){
    document.getElementsByClassName('input')[0].classList.remove('shadow');
    return [];
  }
  filterOptionsInitialization(){
    this.filteredOptions = this.myControl.valueChanges
    .pipe(
      // startWith('*'),
      // map(value => this._filter(value))
      startWith(''),
      map(value => value.length >= 1 ? this._filter(value): this.empty())
    );
    this.common.autoSuggestion().subscribe((response)=>{
      this.common.serverResponse=this.common.errorResponse=this.common.successResponse=false;
      if(response['autoSuggestionKeywords']){
        this.autocomplete_options=response['autoSuggestionKeywords'];
      }
      else{
        this.common.errorResponse=true;
        this.common.serverTextMessage="Error in fetching autosuggestion list";
      }
    })
  }
  focusIn(){
    this.audio=false;
    this.filterOptionsInitialization();
  }
  focusOut(){
this.audio=true;
// this.closeDropdown()
  }
  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
      if(this.autocomplete_options.filter(option => option.toLowerCase().includes(filterValue)).length!=0){
        document.getElementsByClassName('input')[0].classList.add('shadow');
      }
if(this.keyCode==40 || this.keyCode==38)
{
  this.keyCode=0;
  return this.autocomplete_options.filter(option => option.toLowerCase().includes(this.typedValue));
}
else
{
  this.keyCode=0;
  if(this.autocomplete_options.filter(option => option.toLowerCase().includes(filterValue)).length==0){
    document.getElementsByClassName('input')[0].classList.remove('shadow');
  }
  return this.autocomplete_options.filter(option => option.toLowerCase().includes(filterValue));
} 
}
  select(e){
   this.option_selected=true;
  }
  closeDropdown(){
    document.getElementsByClassName('input')[0].classList.remove('shadow');
    this.filteredOptions=this.myControl.valueChanges;
  }
    addBackground(e){
      if(e.keyCode==40){
        this.keyCode=e.keyCode;
        if(document.getElementsByClassName('optionActive').length==0)
        {
          document.getElementsByClassName('autocomplete-list')[0].firstElementChild.classList.add('optionActive');
          this.query=document.getElementsByClassName('autocomplete-list')[0].firstElementChild.firstElementChild.innerHTML;
        }else{
          let newActive:any=document.getElementsByClassName('optionActive')[0].nextSibling;
          document.getElementsByClassName('optionActive')[0].classList.remove('optionActive');
          newActive.classList.add('optionActive');
          this.query=newActive.innerText;
        }
      }
      else if(e.keyCode==38){
        this.keyCode=e.keyCode;
        let newActive:any=document.getElementsByClassName('optionActive')[0].previousSibling;
        document.getElementsByClassName('optionActive')[0].classList.remove('optionActive');
        newActive.classList.add('optionActive');
        this.query=newActive.innerText;
      }
      else if(e.keyCode==13)
      this.User_to_bot(e);
      else{
        this.typedValue = e.target.value;
        if(this.query==""){
          document.getElementsByClassName('input')[0].classList.remove('shadow');
        }
      }
    }
    executeQuery(e){
      this.query=e. currentTarget.innerText;
      this.User_to_bot(e);
    }
  User_to_bot(e) {
    this.chart.columNames=[];
    this.chart.columnWithTypes=[];
    document.getElementsByClassName('input')[0].classList.remove('shadow');
    this.common.serverResponse=this.common.errorResponse=this.common.successResponse=false;
    this.common.loading=true;
    this.common.serverResponse=false;
    this.common.serverTextMessage="";
    this.image_path="";
    if (this.query != "") {
      if(e.currentTarget.className!='audio'){
      this.message = this.query;
      
      let month = formatDate(new Date(), 'MM', 'en');
      let date = moment(month, 'MM').format('MMMM') + " " + formatDate(new Date(), 'dd yyyy', 'en');

      let time = moment().format('hh:mm A');
      
      this.common.response_from_bot(this.query).subscribe((response)=>{
        if(response['statusCode']==200)
        {
        this.res=true;
        if (response['serverTextMessage'] != null && response['serverTextMessage'] != 'null' && response['serverTextMessage'] != "")
        {
          this.common.serverResponse=true;
          this.isMessage=true;
          this.message = response['serverTextMessage'];
          this.common.serverTextMessage=response['serverTextMessage'];
          this.common.loading=false;
        }
        else
        this.isMessage=false;

        if(response['searchShowImage']=="true" && response['serverImageMessage']!=undefined){
          this.noImage = false;
          this.image_path =response['serverImageMessage'];
        }
        else
        {
          this.noImage=true;
        }

        if (response['tabularData']!=undefined) {
          this.table = true;
          this.tabularContent = response['tabularData'];
          this.chart.columNames.push(response['tabularData']['columNames']);
          this.chart.columnWithTypes.push(response['tabularData']['columnWithTypes'])

          //this.chart.columNames[1]=int,this.chart.columNames[0]=obj
          let columnWithTypes=this.chart.columnWithTypes;
          if(columnWithTypes[0].dataType=='int64'||columnWithTypes[0].dataType=='float64')
          {
            this.chart.columNames[1]=columnWithTypes[0][0].columnName;
            this.chart.columNames[0]=columnWithTypes[0][1].columnName;
            }
            else
            {
              this.chart.columNames[0]=columnWithTypes[0][0].columnName;
              this.chart.columNames[1]=columnWithTypes[0][1].columnName;
            }
          this.common.formatTableData(this.tabularContent).subscribe((res)=>{
           
            if(res)
            {
              this.chart.ispopUp=false;
              Object.values(res).map(ele=>{
                ele[this.chart.columNames[1]]=parseInt(ele[this.chart.columNames[1]].replace(/,/g, ""));
              })
              this.common.cardsArray[this.common.cardsArray.length-1].sortedData=res;
              this.common.cardsArray[this.common.cardsArray.length-1].tableJsonData=res;
              this.chart.findGraph(res,this.common.cardsArray.length-1);
            }
            else if (res['status']=="error"){
              // this.commonService.openSnackBar(response['description'],"");
            }
          },
            error=>{
              if(error['error']['statusCode']!="dhi_data_not_found_error")
              this.common.errorResponse=true;
              this.common.serverTextMessage=error['error']['description'];
      });
        }
        else
        this.table = false;
        let month = formatDate(new Date(), 'MM', 'en');
        let date = moment(month, 'MM').format('MMMM') + " " + formatDate(new Date(), 'dd yyyy', 'en');
  
        let time = moment().format('hh:mm A');
        let storyItem = {
        "title":response['title'],
        "order":"",
        "response":this.res,
        "userQueryMessage":this.message,
        "serverTextMessage":this.common.serverTextMessage, 
        "isMessage":this.common.serverResponse,
        "resdate": date,
        "restime": time,
        "serverImageMessage": this.image_path,
        "tabularData":this.tabularContent,
        "itemId":response['storyItemId'],
        "color":"#a6ce39",
        "fontFamily":"Montserrat-Regular",
        "unit":"none",
        "graphType":response['tabularData']['graphTypes'][0],
        "graphTypes":response['tabularData']['graphTypes'],
        "columnWithTypes":response['tabularData']['columnWithTypes'],
        "fontSize":10,
        "sortedData":{},
        "sortObj":{},
        "columNames":response['tabularData']['columNames']
      };
        this.common.loading=false;

        if(!storyItem.isMessage)
        {
          if(this.common.isStoryOpen)
          {
            this.common.openedStory.push(storyItem);
            this.common.cardsArray=this.common.openedStory;
          }
          else{
            this.common.queryResponse.push(storyItem);
            this.common.cardsArray=this.common.queryResponse;
          }
        }

        }
        else{
          this.common.loading=false;
          this.common.serverResponse=true;
          this.common.serverTextMessage=response['description'];
        }
      },
        error=>{
          this.common.loading=false;
          this.common.errorResponse=true;
          this.common.serverTextMessage=error['message'];
  });
  this.query = "";
  this.recording = false;
    }
    else{
      if(this.query!=""){
        this.query="";
      }
      this.recording = true;
      this.common.loading=false;
      this.activateRecording();
    }
  }
    else {
      if(this.query!=""){
        this.query="";
      }
      this.recording = true;
      this.common.loading=false;
      this.activateRecording();
    }
    // console.log("cardsArray",this.common.cardsArray)
}
  activateRecording(): void {
    this.showSearchButton = false;
    this.speechRecognitionService.record()
      .subscribe(
        //listener
        (value) => {
          this.speechData = value;
          this.recording = false;
          this.query = value;
          this.input.nativeElement.focus();
          this.speechRecognitionService.DestroySpeechObject();
        },
        //errror
        (err) => {
          this.common.loading=false;
          if (err.error == "no-speech") {
            console.log("--restatring service--");
            this.activateRecording();
          }
        },
        //completion
        () => {
          this.showSearchButton = true;
          console.log("--complete--");
          // this.activateRecording();
        });
  }
  onFilterOptionSelected(e){
    console.log("option selected",e);
  }
}

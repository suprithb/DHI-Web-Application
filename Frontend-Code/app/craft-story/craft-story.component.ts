import { Component, OnInit, ViewChild } from '@angular/core';
import { trigger, state, style, animate, transition, query, stagger, group } from '@angular/animations'
import {
  CdkDrag,
  CdkDragStart,
  CdkDropList, CdkDropListGroup, CdkDragMove, CdkDragEnter,
  moveItemInArray
} from "@angular/cdk/drag-drop";
import {Router} from '@angular/router';
import {ViewportRuler} from "@angular/cdk/overlay";
import { CommonService } from '../../services/common.service';
import {MatDialog, MatDialogRef,MatTableDataSource,MatSort} from '@angular/material';
import {CraftStoryDialogComponent} from '../craft-story-dialog/craft-story-dialog.component';
import { ImageDialogComponent } from '../image-dialog/image-dialog.component';
import { ExportToFileService } from '../../services/export-to-file.service';
import { formatDate } from '@angular/common';
import * as moment from 'moment';
import {saveAs as importedSaveAs} from "file-saver";
import { ChartsService } from '../../services/charts.service';
import * as d3 from 'd3-3';

export interface DialogData {
  imageUrl: string;
  storyName:string;
  tableData:any;
  cardsData:any;
}

@Component({
  selector: 'app-craft-story',
  templateUrl: './craft-story.component.html',
  styleUrls: ['./craft-story.component.css'],
  animations: [
    trigger('flipState', [
      state('active', style({
        transform: 'rotateY(179.9deg)'
      })),
      state('inactive', style({
        transform: 'rotateY(0)'
      })),
      transition('active => inactive', animate('500ms ease-out')),
      transition('inactive => active', animate('500ms ease-in'))
    ])  
  ]
})
export class CraftStoryComponent implements OnInit {
  public data:any = [
    {columnName: "column1", description: 'Lorem Ipsum ', dataType: 'String', aliasName: 'aliasName',readonlyValue:true},
    {columnName: "column2", description: 'Lorem Ipsum ', dataType: 'String', aliasName: 'aliasName',readonlyValue:true},
    {columnName: "column3", description: 'Lorem Ipsum ', dataType: 'String', aliasName: 'aliasName',readonlyValue:true},
    {columnName: "column4", description: 'Lorem Ipsum ', dataType: 'String', aliasName: 'aliasNameBe',readonlyValue:true},
    {columnName: "column5", description: 'Lorem Ipsum ', dataType: 'String', aliasName: 'aliasNameB',readonlyValue:true},
    {columnName: "column6", description: 'Lorem Ipsum ', dataType: 'String', aliasName: 'aliasNameC',readonlyValue:true}
    ]
  @ViewChild(CdkDropListGroup) listGroup: CdkDropListGroup<CdkDropList>;
  @ViewChild(CdkDropList) placeholder: CdkDropList;
  @ViewChild('width') width;
  // @ViewChild(examplBox) placeholder: exampleBox;

  public target: CdkDropList;
  public targetIndex: number;
  public source: CdkDropList;
  public sourceIndex: number;
  public dragIndex: number;
  public activeContainer;
  imageUrl:string="";
  storyName:string="";
  autosuggestionwords:any=[];
  dataSource = new MatTableDataSource();
  @ViewChild(MatSort) sort: MatSort;

  public items: any = [{"item":1,"flip":"inactive"},
  {"item":2,"flip":"inactive"},
  {"item":3,"flip":"inactive"},
  {"item":4,"flip":"inactive"},
  {"item":4,"flip":"inactive"},
  {"item":6,"flip":"inactive"}];
  
  displayOptionsDiv:Boolean=false;
  indexNo:any;
  displayOptionsIcon:Boolean=false;

  constructor(private router: Router,
    private viewportRuler: ViewportRuler,
    public commonService:CommonService,
    public dialog: MatDialog,
    public exportFile:ExportToFileService,
    public chart:ChartsService) 
    { 
        this.target = null;
        this.source = null;
        this.commonService.loading=false;
    }
  
  openDialog(): void {
    const dialogRef = this.dialog.open(CraftStoryDialogComponent, {
      width: '30%',
      height:'158px',
      data: {imageUrl: this.imageUrl, storyName: this.storyName,cardsData:this.commonService.cardsArray}
    });

    dialogRef.afterClosed().subscribe(result => {
      this.storyName = result;
    });
  }
  openDialogImage(item,index): void {
    const dialogRef = this.dialog.open(ImageDialogComponent, {
      // data:{imageUrl:url,table:tableData,title:title,description:description,index:index,color:color,fontFamily:fontFamily,unit:unit,graphType:graphType,fontSize:fontSize},
      data:{item:item,index:index},
      width: '1000px'
    });
  dialogRef.afterClosed().subscribe(data => {
    this.chart.ispopUp=false;
    this.commonService.cardsArray[dialogRef.componentInstance.data.index].sortedData=dialogRef.componentInstance.sortedData;
    this.chart.findGraph(dialogRef.componentInstance.sortedData,dialogRef.componentInstance.data.index);
    });
  }

  ngOnInit() {
    this.commonService.craftActive=true;
    this.commonService.cardsArray=[];
    
    if(this.commonService.isStoryOpen)
      this.commonService.cardsArray=this.commonService.openedStory;
    else
    this.commonService.cardsArray=this.commonService.queryResponse;

    setTimeout(()=>{
      this.chart.reRenderCharts(this.commonService.cardsArray);
    },200)
    
  }

  ngAfterViewInit() {
    let phElement = this.placeholder.element.nativeElement;
    phElement.style.display = 'none';
    phElement.parentElement.removeChild(phElement);
  }
  refresh(){
    this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
    let month = formatDate(new Date(), 'MM', 'en');
    let date = formatDate(new Date(), 'dd', 'en')+" " +moment(month, 'MM').format('MMMM') +" "+formatDate(new Date(), 'yyyy', 'en');
    let time = moment().format('hh:mm A');
    this.commonService.cardRefreshDate=date;

    this.commonService.loading=true;
    this.commonService.getStories().subscribe((response)=>{
      if(response)
      {
        this.commonService.loading=false;
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
  delete(index){
this.commonService.cardsArray.splice(index,1);
  }
  toggleFlip(item) {
    item.flip = (item.flip == 'inactive') ? 'active' : 'inactive';
  }
  dragMoved(e: CdkDragMove) {
    let point = this.getPointerPositionOnPage(e.event);

    this.listGroup._items.forEach(dropList => {
      if (__isInsideDropListClientRect(dropList, point.x, point.y)) {
        this.activeContainer = dropList;
        return;
      }
    });
  }
  autoSuggestion(){
    this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
    this.commonService.autoSuggestion().subscribe((response)=>{
      if(response)
      {
       this.autosuggestionwords=response['autoSuggestionKeywords'];
      }
      else if (response['status']=="error"){
      }
    },
      error=>{
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=error['error']['description'];
});
  }
  setTableData(item){
    let data:any=item.sortedData;
    this.dataSource= new MatTableDataSource(data);
    return this.dataSource;
  }
  checkType(n){
    return isNaN(n);
  }
  clearStories(){
    this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
    this.commonService.cardsArray=[];
    this.commonService.queryResponse=[];
  }
  closeStory(){
    this.commonService.cardsArray=this.commonService.queryResponse;
    this.commonService.isStoryOpen=false;
    setTimeout(()=>{
      this.chart.reRenderCharts(this.commonService.cardsArray);
    },500)
    
  }
  export(data){
    this.commonService.formatTableData(data).subscribe((response)=>{
      if(response)
      {
        this.exportFile.exportAsExcelFile(response, 'sample');
      }
      else if (response['status']=="error"){
      }
    },
      error=>{
        if(error['error']['statusCode']!="dhi_data_not_found_error")
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=error['error']['description'];
});
  }
  dataToDownload(data: Blob) {
    const blob = new Blob([data], { type: 'pptx' });
    importedSaveAs(data, this.commonService.openedStoryTitle+".pptx");
  }
  createPPT(title){
    this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
    this.commonService.createPPT(title).subscribe((response)=>{
      if(response['status']=="success")
      {
        this.commonService.downloadUrl(response['pptDownloadUri']).subscribe(data=>this.dataToDownload(data)),
        error=>console.log(error),
        ()=>console.log("ok");
      }
      else if (response['status']=="error"){
                this.commonService.errorResponse=true;
                this.commonService.serverTextMessage=response['description'];
      }
    },
      error=>{
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=error['message'];
});
  }
  saveOpenedStory(){
    this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
    if(this.commonService.cardsArray.length!=0){
    let storyObj={
      "storyTitle":this.commonService.openedStoryTitle,
      "storyItem":this.commonService.cardsArray
    }
    console.log(storyObj)
    this.commonService.addStory(storyObj).subscribe((response)=>{
      if(response['status']=="Success")
      {
        this.commonService.successResponse=true;
        this.commonService.serverTextMessage="Story saved successfully";
      }
      else {
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=response['description'];
      }
    },
      error=>{
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=error['message'];
  });
}
else{
  this.commonService.errorResponse=true;
  this.commonService.serverTextMessage="There are no elements for story to save";
}
  }
  ngOnDestroy() {
    this.commonService.craftActive=false;
  }
  isnumber(num){
return isNaN(parseFloat(num));
  }
  dropListDropped() {
    if (!this.target)
      return;

    let phElement = this.placeholder.element.nativeElement;
    let parent = phElement.parentElement;
    phElement.style.display = 'none';
    parent.removeChild(phElement);
    parent.appendChild(phElement);
    parent.insertBefore(this.source.element.nativeElement, parent.children[this.sourceIndex]);

    this.target = null;
    this.source = null;

    if (this.sourceIndex != this.targetIndex)
      moveItemInArray(this.commonService.cardsArray, this.sourceIndex, this.targetIndex);
  }

  dropListEnterPredicate = (drag: CdkDrag, drop: CdkDropList) => {
    if (drop == this.placeholder)
      return true;

    if (drop != this.activeContainer)
      return false;

    let phElement = this.placeholder.element.nativeElement;
    let sourceElement = drag.dropContainer.element.nativeElement;
    let dropElement = drop.element.nativeElement;

    let dragIndex = __indexOf(dropElement.parentElement.children, (this.source ? phElement : sourceElement));
    let dropIndex = __indexOf(dropElement.parentElement.children, dropElement);

    if (!this.source) {
      this.sourceIndex = dragIndex;
      this.source = drag.dropContainer;

      phElement.style.width = sourceElement.clientWidth + 'px';
      phElement.style.height = sourceElement.clientHeight + 'px';
      
      sourceElement.parentElement.removeChild(sourceElement);
    }

    this.targetIndex = dropIndex;
    this.target = drop;

    phElement.style.display = '';
    dropElement.parentElement.insertBefore(phElement, (dropIndex > dragIndex 
      ? dropElement.nextSibling : dropElement));

    this.placeholder.enter(drag, drag.element.nativeElement.offsetLeft, drag.element.nativeElement.offsetTop);
    return false;
  }
  
  /** Determines the point of the page that was touched by the user. */
  getPointerPositionOnPage(event: MouseEvent | TouchEvent) {
    // `touches` will be empty for start/end events so we have to fall back to `changedTouches`.
    const point = __isTouchEvent(event) ? (event.touches[0] || event.changedTouches[0]) : event;
        const scrollPosition = this.viewportRuler.getViewportScrollPosition();

        return {
            x: point.pageX - scrollPosition.left,
            y: point.pageY - scrollPosition.top
        };
    }

    copyToClip(i){
      var el = document.getElementById('image'+i);
      el.setAttribute('contenteditable','true');
      el.focus();
      document.execCommand('selectAll');
      document.execCommand('copy');
      el.setAttribute('contenteditable','false');
      el.blur();
    }
    changeStyle($event,i){
      $event.preventDefault();
      this.indexNo=i;
      this.displayOptionsIcon=$event.type=='mouseover'?true:false;
      }
      displayoptions(e,i){
        e.preventDefault();
        this.displayOptionsDiv=e.type=='mouseover'?true:false;
        this.indexNo=i;
      } 

}
function __indexOf(collection, node) {
  return Array.prototype.indexOf.call(collection, node);
};

/** Determines whether an event is a touch event. */
function __isTouchEvent(event: MouseEvent | TouchEvent): event is TouchEvent {
  return event.type.startsWith('touch');
}

function __isInsideDropListClientRect(dropList: CdkDropList, x: number, y: number) {
  const {top, bottom, left, right} = dropList.element.nativeElement.getBoundingClientRect();
  return y >= top && y <= bottom && x >= left && x <= right; 
}




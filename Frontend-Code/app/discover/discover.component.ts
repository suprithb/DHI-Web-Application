import { Component, OnInit,ViewChild,ElementRef} from '@angular/core';
import {MatTableDataSource} from '@angular/material';
import {SelectionModel} from '@angular/cdk/collections';
// import { ConversationHttpService } from '../../services/conversation-http.service';
import { element } from 'protractor';
import { CommonService } from '../../services/common.service';
import {Router} from '@angular/router';

export interface PeriodicElement {
  description: string;
  columnName: string;
  dataType: string;
  aliasName: string;
  readonlyValue:Boolean;
}
const ELEMENT_DATA: PeriodicElement[] = [];

@Component({
  selector: 'app-discover',
  templateUrl: './discover.component.html',
  styleUrls: ['./discover.component.css']
})
export class DiscoverComponent implements OnInit {
  indexNo:any;
  @ViewChild('scrollMe') private myScrollContainer: ElementRef;
  @ViewChild('table') private table: ElementRef;
  addrowIcon:Boolean=false;
  tdNo:number;
  description:any=["hs","sdgs"];
  aliasName:any=[];
  dataType:any=[];
  editable:Boolean;
  duplicateColummnName:Boolean=false;
  displayedColumns: string[] = ['select','columnName','dataType','description', 'aliasName'];
  dataTypeName:string[]=['Integer','Decimal','Date/Time','Logical','String'];
  dataSource = new MatTableDataSource<PeriodicElement>(ELEMENT_DATA);
  selection = new SelectionModel<PeriodicElement>(true, []);
  
  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
  constructor(public commonService:CommonService,public router:Router) {
    this.commonService.discoverActive=true;
    this.editable=false;
    this.commonService.serverResponse=false;
    this.commonService.serverTextMessage="";
   }

  ngOnInit() {
    let i;
    this.commonService.getDiscoverData().subscribe(data=>{
      this.dataSource.data=data['discovery'];
      this.commonService.discoverTableData.next(data['discovery']);
    })
    this.commonService.discoverTableData.subscribe(value=>{
      this.dataSource.data=Object.values(value);
    })
  }
addRowIcon($event,i,num){
  $event.preventDefault();
  this.indexNo=i;
  this.addrowIcon=$event.type=='mouseover'?true:false;
  this.tdNo=num;
  }
editContent(i){
this.dataSource.data[i].readonlyValue=!this.dataSource.data[i].readonlyValue;
}

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
        this.selection.clear() :
        this.dataSource.data.forEach(row => this.selection.select(row));
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: PeriodicElement): string {
    if (!row) {
      return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.columnName + 1}`;
  }
  Save(){
    this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
    let obj={
      "discovery":this.dataSource.data
    }
    this.commonService.loading=true;
    this.commonService.saveDiscoverData(obj).subscribe((response)=>{
      if(response["status"]=="success"){
        this.commonService.loading=false;
        this.commonService.successResponse=true;
        this.commonService.serverTextMessage="Successfully saved the data"
        this.commonService.discoverActive=false;
        this.commonService.craftActive=true;
        this.router.navigateByUrl('/craft-stories')
      }
      else{
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
  scrollToBottom(): void {
    try {
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch (err) {
    }
  }
  addRow(){
    const currrentValue=this.commonService.discoverTableData.value;
    
    currrentValue[currrentValue.length-1].readonlyValue=true;//to make last column non-editable
    const updatedValue=[...currrentValue,{columnName: "column0", description: '', dataType: 'Derived', aliasName: '',readonlyValue:false, isDisabled:true,}]
    this.commonService.discoverTableData.next(updatedValue);
    this.scrollToBottom();
  }
  checkcolumnName(e){
    let i
    for(i=0;i<this.commonService.discoverTableData.value.length-1;i++){
      if(this.commonService.discoverTableData.value[i].columnName==e.target.value){
        e.currentTarget.classList+=' red-border';
        break;
      }
      else{
      e.currentTarget.classList.remove('red-border');
      }
    }
  }
  deleteRows(){
    let currentTable=this.commonService.discoverTableData.value;
    let i;
    this.selection.selected.forEach(element=>{
    for(i=0;i<currentTable.length;i++){
      if(currentTable[i].columnName==element.columnName){
        currentTable.splice(i,1);
        break;
      }
    }
    })
    this.commonService.discoverTableData.next(currentTable);
    this.selection.clear();
  }
  ngOnDestroy(){
    this.commonService.discoverActive=false;
  }
}


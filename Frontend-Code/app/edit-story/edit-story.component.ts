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
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {CraftStoryDialogComponent} from '../craft-story-dialog/craft-story-dialog.component';
import { ImageDialogComponent } from '../image-dialog/image-dialog.component';
import { ExportToFileService } from '../../services/export-to-file.service';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';


@Component({
  selector: 'app-edit-story',
  templateUrl: './edit-story.component.html',
  styleUrls: ['./edit-story.component.css']
})
export class EditStoryComponent implements OnInit {
  filteredOptions: Observable<string[]>;
  autocomplete_options:any=[];
  myControl = new FormControl();
  query:string;
  typedValue = '';
  keyCode:number=0;
  constructor(private router: Router,
    public commonService:CommonService) 
    { 
    }

  ngOnInit() {
    this.filteredOptions = this.myControl.valueChanges
    .pipe(
      // startWith('*'),
      // map(value => this._filter(value))
      startWith(''),
      map(value => value.length >= 1 ? this._filter(value): [])
    );
    this.commonService.autoSuggestion().subscribe((response)=>{
      this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
      if(response['autoSuggestionKeywords']){
        this.autocomplete_options=response['autoSuggestionKeywords'];
      }
      else{
        // this.common.openSnackBar("Error in fetching autosuggestion list","");
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage="Error in fetching autosuggestion list";
      }
    })
  }
  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
      if(this.autocomplete_options.filter(option => option.toLowerCase().includes(filterValue)).length==0){
        document.getElementById('body').classList.remove('body');
      }
if(this.keyCode==40 || this.keyCode==38)
{
  this.keyCode=0;
  // return this.autocomplete_options.filter(option =>true);
  return this.autocomplete_options.filter(option => option.toLowerCase().includes(this.typedValue));

  
}
else
{
  this.keyCode=0;
  return this.autocomplete_options.filter(option => option.toLowerCase().includes(filterValue));
} 
}
closeDropdown(){
  this.filteredOptions=this.myControl.valueChanges;
}
  addBackground(e){
    if(e.keyCode==40){
      this.keyCode=e.keyCode;
      if(document.getElementsByClassName('active').length==0)
      {
        document.getElementsByClassName('autocomplete-list')[0].firstElementChild.classList.add('active');
        this.query=document.getElementsByClassName('autocomplete-list')[0].firstElementChild.firstElementChild.innerHTML;
      }else{
        let newActive:any=document.getElementsByClassName('active')[0].nextSibling;
        document.getElementsByClassName('active')[0].classList.remove('active');
        newActive.classList.add('active');
        this.query=newActive.innerText;
      }
    }
    else if(e.keyCode==38){
      this.keyCode=e.keyCode;
      let newActive:any=document.getElementsByClassName('active')[0].previousSibling;
      document.getElementsByClassName('active')[0].classList.remove('active');
      newActive.classList.add('active');
      this.query=newActive.innerText;
    }
    else{
      this.typedValue = e.target.value;
    }
  }
  }

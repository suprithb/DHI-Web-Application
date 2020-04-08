import { Component, OnInit,ViewChild,ElementRef } from '@angular/core';
import {MatDialog, MatDialogRef,MatTableDataSource,MatSort} from '@angular/material';
import {MAT_DIALOG_DATA} from '@angular/material';
import { Inject } from '@angular/core';
import { ExportToFileService } from '../../services/export-to-file.service';
import { CommonService } from '../../services/common.service';
import { ChartsService } from '../../services/charts.service';
import { ColorPickerModule } from 'ngx-color-picker';
import { trigger, state, style, transition,
  animate, group, query, stagger, keyframes
} from '@angular/animations';
import html2canvas from 'html2canvas';
import { _ } from 'underscore';

@Component({
  selector: 'app-image-dialog',
  templateUrl: './image-dialog.component.html',
  styleUrls: ['./image-dialog.component.css'],
  animations: [
    trigger('fadeInOut', [
      transition(':enter', [
        style({transform: 'translateY(-100%)'}),
        animate('200ms ease-in', style({transform: 'translateY(0%)'}))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({transform: 'translateY(-100%)'}))
      ])
    ])
  ]
})
export class ImageDialogComponent implements OnInit {
  fontFamily:any;
  public color: string = '#ffa500';
  fontSize=[10,12,14]
  displayedColumns:any=[];
  dataSource = new MatTableDataSource();
  sortedData=[];
  isEditOpen:boolean=false;
  animationState = 'out';
  SIUnit:String='';
  capturedImage;
  @ViewChild(MatSort) sort: MatSort;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
  public dialogRef: MatDialogRef<ImageDialogComponent>,
  public commonService:CommonService,
  public exportFile:ExportToFileService,
  public chart:ChartsService) {
   }
  onNoClick(): void {
    this.dialogRef.close();
  }
  ngOnInit() {
    this.color=this.commonService.cardsArray[this.data.index].color;
    this.data.item.tabularData.columNames.forEach(element => {
      this.displayedColumns.push(element);
    });

  let data:any=this.data.item.sortedData;
  this.dataSource= new MatTableDataSource(data);
  this.dataSource.sort = this.sort;
  this.dataSource.connect().subscribe(d => 
  {
    this.sortedData=d;
    this.chart.findGraph(d,this.data.index)
  });
  this.chart.ispopUp=true;
  // this.chart.chartOptions=res[chartOptions]           
  this.chart.findGraph(data,this.data.index);

this.commonService.getFonts().subscribe((res)=>{
this.fontFamily=res;
},
  error=>{
    this.commonService.errorResponse=true;
    this.commonService.serverTextMessage=error['error']['description'];
});
  }
  export(data){
    this.commonService.serverResponse=this.commonService.errorResponse=this.commonService.successResponse=false;
    this.commonService.formatTableData(data).subscribe((response)=>{
      if(response)
      {
        this.exportFile.exportAsExcelFile(response, 'sample');
      }
      else if (response['status']=="error"){
        // this.commonService.openSnackBar(response['description'],"");
      }
    },
      error=>{
        if(error['error']['statusCode']!="dhi_data_not_found_error")
        this.commonService.errorResponse=true;
        this.commonService.serverTextMessage=error['error']['description'];
});
  }
  isnumber(num){
    return isNaN(parseFloat(num));
      }
      changeFontFamily(fontFamily,index){
        this.commonService.cardsArray[index].fontFamily=fontFamily;
        this.chart.ispopUp=true;
        this.chart.findGraph(this.sortedData,this.data.index);
    }

      changeColor(color,index){
        this.commonService.cardsArray[index].color=this.color=color;
        this.chart.ispopUp=true;
        this.chart.findGraph(this.sortedData,this.data.index);
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
    checkType(n){
      return isNaN(n);
    }
      changeGraph(graphType,index){
          this.commonService.cardsArray[index].graphType=graphType;
          this.chart.findGraph(this.dataSource.data,this.data.index);
      }
      changeUnit(unit,index,data){
        this.chart.columNames=data.columNames;
        this.commonService.formatTableData(data).subscribe((res)=>{
          if(res)
          {
            Object.values(res).map(ele=>{
              if(ele[this.chart.columNames[1]]!=undefined)
              ele[this.chart.columNames[1]]=parseInt(ele[this.chart.columNames[1]].replace(/,/g, "")),unit,index;
            })
            this.commonService.cardsArray[index].unit=unit;
        
            //sorting
            let newSortedData=Object.values(res);
            if(this.commonService.cardsArray[index].sortObj.direction=='asc')
            newSortedData=_.sortBy(newSortedData,this.commonService.cardsArray[index].sortObj.active);
            else if(this.commonService.cardsArray[index].sortObj.direction=='desc')
            newSortedData=_.sortBy(newSortedData,this.commonService.cardsArray[index].sortObj.active).reverse();
            else
            newSortedData=Object.values(res);

            newSortedData.map(ele=>{
              if(ele[this.chart.columNames[1]]!=undefined)
              ele[this.chart.columNames[1]]=this.format(ele[this.chart.columNames[1]],unit,index);
            })

            this.dataSource.data=newSortedData;
            this.chart.findGraph(newSortedData,index);
          }
          else if (res['status']=="error"){
            // this.commonService.openSnackBar(response['description'],"");
          }
        },
          error=>{
            if(error['error']['statusCode']!="dhi_data_not_found_error")
            this.commonService.errorResponse=true;
            this.commonService.serverTextMessage=error['error']['description'];
    });
      }
      changeFontSize(fontSize,index){
        this.commonService.cardsArray[index].fontSize=fontSize;
        this.chart.findGraph(this.dataSource.data,index);
      }
      toggle(){
        this.isEditOpen=!this.isEditOpen;
        if(this.isEditOpen==true)
        this.animationState="out";
        else
        this.animationState="in";
      }
      downloadImage(){
        html2canvas(document.getElementById('image-1')).then(canvas => {
         this.capturedImage = canvas.toDataURL();

         const link = document.createElement('a');
         link.href = this.capturedImage;
         link.download = 'image.png';
         link.click();

        });
      }
      sortData(e){
        this.commonService.cardsArray[this.data.index].sortObj={};
        this.commonService.cardsArray[this.data.index].sortObj=e;

        let newSortedData=this.dataSource.data;
            if(this.commonService.cardsArray[this.data.index].sortObj.direction=='asc')
            newSortedData=_.sortBy(newSortedData,this.commonService.cardsArray[this.data.index].sortObj.active);
            else if(this.commonService.cardsArray[this.data.index].sortObj.direction=='desc')
            newSortedData=_.sortBy(newSortedData,this.commonService.cardsArray[this.data.index].sortObj.active).reverse();
            else
            newSortedData=this.dataSource.data;

            this.dataSource.data=newSortedData;
    }
  }

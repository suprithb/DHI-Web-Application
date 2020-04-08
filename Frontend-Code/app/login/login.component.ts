import { Component, OnInit, ViewChild,ElementRef } from '@angular/core';
import { CommonService } from '../../services/common.service';
import {Router} from '@angular/router';
import { BarChartComponent } from '../bar-chart/bar-chart.component';
import * as d3 from 'd3-3';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  public targetIndex: number;
  public sourceIndex: number;
  public dragIndex: number;
  public activeContainer;
  isappear: Boolean = true;
  username:string;
  password:string;
  data:any=[];
  @ViewChild("checkbox") checkbox: ElementRef;
  constructor(public common:CommonService,private router: Router) { 
    this.data=[
      {
        "letter": "A",
        "frequency": 0.08167
      },
      {
        "letter": "B",
        "frequency": 0.01492
      },
      {
        "letter": "C",
        "frequency": 0.02782
      },
      {
        "letter": "D",
        "frequency": 0.04253
      },
      {
        "letter": "E",
        "frequency": 0.12702
      },
      {
        "letter": "F",
        "frequency": 0.02288
      },
      {
        "letter": "G",
        "frequency": 0.02015
      },
      {
        "letter": "H",
        "frequency": 0.06094
      },
      {
        "letter": "I",
        "frequency": 0.06966
      },
      {
        "letter": "J",
        "frequency": 0.00153
      },
      {
        "letter": "K",
        "frequency": 0.00772
      },
      {
        "letter": "L",
        "frequency": 0.04025
      },
      {
        "letter": "M",
        "frequency": 0.02406
      },
      {
        "letter": "N",
        "frequency": 0.06749
      },
      {
        "letter": "O",
        "frequency": 0.07507
      },
      {
        "letter": "P",
        "frequency": 0.01929
      },
      {
        "letter": "Q",
        "frequency": 0.00095
      },
      {
        "letter": "R",
        "frequency": 0.05987
      },
      {
        "letter": "S",
        "frequency": 0.06327
      },
      {
        "letter": "T",
        "frequency": 0.09056
      },
      {
        "letter": "U",
        "frequency": 0.02758
      },
      {
        "letter": "V",
        "frequency": 0.00978
      },
      {
        "letter": "W",
        "frequency": 0.0236
      },
      {
        "letter": "X",
        "frequency": 0.0015
      },
      {
        "letter": "Y",
        "frequency": 0.01974
      },
      {
        "letter": "Z",
        "frequency": 0.00074
      }
    ]
    let userObj=localStorage.getItem("userObj");
    if(userObj)
    {
        this.username=JSON.parse(userObj).userName;
        this.password=JSON.parse(userObj).password;
    }
    this.common.rememberMe=JSON.parse(localStorage.getItem("rememberMe"));
  }

  ngOnInit() {
//   let a=[25,10,12,30];
// let svg=d3.select("body")
// .append("svg")
// .attr("width",500)
// .attr("height",600);

// let circle=svg.selectAll("circles").data(a);

// circle.enter().append("circle")
// .attr("cx",(d,i)=>{
//   return d*i;
// })
// .attr("cy",(d,i)=>{
//   return d*i*10
// })
// .attr("r",20)
// .attr("fill",'blue');
  }
  login(){
    let userObj={
      userName:this.username,
      password:this.password
    }
    if(this.checkbox.nativeElement.checked)
    {
      localStorage.setItem("userObj",JSON.stringify(userObj));
      this.common.rememberMe=true;
      localStorage.setItem("rememberMe",JSON.stringify(this.common.rememberMe));
    }
    else
    {
      this.common.rememberMe=false;
      let obj={
        userName:"",
        password:""
      }
      localStorage.setItem("userObj",JSON.stringify(obj));
      localStorage.setItem("rememberMe",JSON.stringify(this.common.rememberMe));
    }

    this.common.username=this.username;
    this.common.login(userObj).subscribe((res)=>{
    if(res['statusCode']==200){
      this.common.roomid=res['chatRoomId'];
      this.common.firstName=res['firstName'];
      this.router.navigateByUrl('/craft-stories');
    }
else{
  this.common.openSnackBar(res['description'],"");
}
        },
    error=>{
    this.common.openSnackBar(error['error']['description'],"");
});
  }

}
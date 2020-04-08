import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import {SplashScreenComponent} from './splash-screen/splash-screen.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { ConnectComponent } from './connect/connect.component';
import { StoriesComponent } from './stories/stories.component';
import { CraftStoryComponent } from './craft-story/craft-story.component';
import { DiscoverComponent } from './discover/discover.component';
import { EditStoryComponent } from './edit-story/edit-story.component';

const routes: Routes = [
  { path:'',component:SplashScreenComponent,data:{animation:'splash'}},
  { path:'login',component:LoginComponent,data:{animation:'login'}},
  // { path:'chat-screen',component:ChatScreenComponent},
  {path:'connect',component:ConnectComponent},
  { path:'sidebar',component:SidebarComponent},
  { path:'stories',component:StoriesComponent},
  { path:'craft-stories',component:CraftStoryComponent},
  { path:'discover',component:DiscoverComponent},
  { path:'edit-stories', component:EditStoryComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes,{useHash:true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }

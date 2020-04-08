import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import {SplashScreenComponent} from './splash-screen/splash-screen.component';
import { DragDropModule } from "@angular/cdk/drag-drop";

import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatTabsModule} from '@angular/material/tabs';
import {MatButtonModule} from '@angular/material/button';
import {MatMenuModule} from '@angular/material/menu';
import {MatSelectModule} from '@angular/material/select';
import {MatTableModule} from '@angular/material/table';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatDialogModule} from '@angular/material/dialog';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatSortModule} from '@angular/material/sort';
import {MatTooltipModule} from '@angular/material/tooltip';

// import { ColorSketchModule } from 'ngx-color/sketch';
import { ColorPickerModule } from 'ngx-color-picker';

import { HeaderComponent } from './header/header.component';
import { ImageDialogComponent } from './image-dialog/image-dialog.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { ConnectComponent } from './connect/connect.component';
import { StoriesComponent } from './stories/stories.component';
import { CraftStoryComponent } from './craft-story/craft-story.component';
import { CraftStoryDialogComponent } from './craft-story-dialog/craft-story-dialog.component';
import { RecentStoriesComponent } from './recent-stories/recent-stories.component';
import { DiscoverComponent } from './discover/discover.component';
import { TableHeaderFilterPipe } from './table-header-filter.pipe';
import { EditStoryComponent } from './edit-story/edit-story.component';
import { BarChartComponent } from './bar-chart/bar-chart.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    SplashScreenComponent,
    HeaderComponent,
    ImageDialogComponent,
    SidebarComponent,
    ConnectComponent,
    StoriesComponent,
    CraftStoryComponent,
    CraftStoryDialogComponent,
    RecentStoriesComponent,
    DiscoverComponent,
    TableHeaderFilterPipe,
    EditStoryComponent,
    BarChartComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MatFormFieldModule,
    MatInputModule,
    MatMenuModule,
    MatTabsModule,
    MatCheckboxModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    MatSnackBarModule,
    MatButtonModule,
    MatSelectModule,
    MatTooltipModule,
    MatTableModule,
    HttpClientModule,
    MatDialogModule,
    MatSortModule,
    MatAutocompleteModule,
    MatCardModule,
    // ColorSketchModule,
    ColorPickerModule,
    DragDropModule
  ],
  entryComponents: [ImageDialogComponent,CraftStoryDialogComponent],
  providers   : [
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

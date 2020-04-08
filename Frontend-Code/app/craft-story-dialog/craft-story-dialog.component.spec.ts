import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CraftStoryDialogComponent } from './craft-story-dialog.component';

describe('CraftStoryDialogComponent', () => {
  let component: CraftStoryDialogComponent;
  let fixture: ComponentFixture<CraftStoryDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CraftStoryDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CraftStoryDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { TestBed } from '@angular/core/testing';

import { ExportToFileService } from './export-to-file.service';

describe('ExportToFileService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ExportToFileService = TestBed.get(ExportToFileService);
    expect(service).toBeTruthy();
  });
});

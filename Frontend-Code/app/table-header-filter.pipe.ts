import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'tableHeaderFilter'
})
export class TableHeaderFilterPipe implements PipeTransform {

  transform(value, args:string[]) : any {
    let tableHeaderFilter = [];
    for (let key in value) {
      tableHeaderFilter.push(key);
    }
    return tableHeaderFilter;
  }

}

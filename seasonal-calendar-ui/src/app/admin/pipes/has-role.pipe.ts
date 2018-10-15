import { Pipe, PipeTransform } from '@angular/core';
import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {hasRole} from "../../shared/roles";


@Pipe({
  name: 'hasRole'
})
export class HasRolePipe implements PipeTransform {

  transform(value: Observable<any>, role: string | Array<string>): Observable<boolean> {

    let roles = role instanceof Array ? role : [role];

    return value.pipe(
      map((value, index) => hasRole(value, roles))
    );
  }

}

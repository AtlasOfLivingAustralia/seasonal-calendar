import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {Injectable} from "@angular/core";
import {UserInfoService} from "../../shared/services/calendar.service";
import {canEditAnyCalendar, canEditCalendar, hasRole} from "../../shared/roles";

@Injectable()
export class RoleCanActivateService implements CanActivate {

  constructor(
    private userInfoService: UserInfoService
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    let allowedRoles: string[] = route.data.roles;
    return hasRoleObservable(this.userInfoService, allowedRoles);
  }

}

@Injectable()
export class UserCanActivateAdminService implements CanActivate {
  constructor(
    private userInfoService: UserInfoService
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    return this.userInfoService.userinfo$.pipe(
      map((value) => canEditAnyCalendar(value))
    )
  }
}

@Injectable()
export class UserCanEditCalendarService implements CanActivate {
  constructor(
    private userInfoService: UserInfoService
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    let id = route.params['id'];
    return this.userInfoService.userinfo$.pipe(
      map((value) => canEditCalendar(value, id))
    )
  }
}

export function hasRoleObservable(service: UserInfoService, allowedRoles: string[]) {
  return service.userinfo$.pipe(
    map((value, index) => hasRole(value, allowedRoles)),
  )
}

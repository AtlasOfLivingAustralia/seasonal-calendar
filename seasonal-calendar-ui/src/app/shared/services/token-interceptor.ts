import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {OidcSecurityService} from "angular-auth-oidc-client";
import {environment} from "../../../environments/environment";

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(
    private oidcSecurityService: OidcSecurityService
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url.startsWith(environment.api)) {

      const token = this.oidcSecurityService.getToken();
      if (token !== '') {
        const tokenValue = 'Bearer ' + token;
        return next.handle(req.clone({
          setHeaders: {
            'Authorization': tokenValue
          }
        }));
      }
    }
    return next.handle(req);
  }

}

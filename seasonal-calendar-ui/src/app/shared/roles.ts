
export const ADMIN_ROLES = ["ROLE_ADMIN", "ROLE_SC_ADMIN"];
export const CALENDAR_ADMIN_ROLE = "ROLE_CALENDAR_ADMIN";
export const CALENDAR_EDITOR_ROLE = "ROLE_CALENDAR_EDITOR";

export function canAdminCalendar(userinfo: any, calendarId: string) {
  if (!userinfo) return false;
  if (hasRole(userinfo, ADMIN_ROLES)) return true;
  if (userinfo.calendarRoles) {
    let roles = userinfo.calendarRoles[CALENDAR_ADMIN_ROLE] || [];
    return roles.indexOf(calendarId) != -1;
  }
  return false;
}

export function canEditCalendar(userinfo: any, calendarId: string) {
  if (!userinfo) return false;
  if (canAdminCalendar(userinfo, calendarId)) return true;
  if (userinfo.calendarRoles) {
    let roles = userinfo.calendarRoles[CALENDAR_EDITOR_ROLE] || [];
    return roles.indexOf(calendarId) != -1;
  }
  return false;
}

export function canEditAnyCalendar(userinfo: any): boolean {
  if (!userinfo) return false;
  if (hasRole(userinfo, ADMIN_ROLES)) return true;
  if (userinfo.calendarRoles) {
    return userinfo.calendarRoles[CALENDAR_ADMIN_ROLE] || userinfo.calendarRoles[CALENDAR_EDITOR_ROLE];
  }
  return false;
}

export function hasRole(userinfo: any, allowedRoles: string[]) {
  if (!userinfo) return false;
  let roles = userinfo.role || [];
  if (userinfo.attributes) {
    roles = roles.concat(userinfo.attributes.role || []);
  }
  for (let allowedRole of allowedRoles) {
    if (userinfo && roles.indexOf(allowedRole) != -1) {
      return true;
    }
  }
  return false;
}

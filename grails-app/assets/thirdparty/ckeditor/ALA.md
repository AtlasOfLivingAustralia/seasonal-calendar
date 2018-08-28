When updating ckeditor you must manually remove the ?t parameter from any 
external resources loaded in CSS files (ie do a find replace on all .css files
and replace any url\((.*)?t=.*\) with url($1)) otherwise the Grails resources
plugin gets confused.

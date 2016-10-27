environments {
    development {
        grails {
            mongo {
                host = "localhost"
                port = "27017"
                databaseName = "seasonal-calendar"
            }
        }
    }
    test {
        grails {
            mongo {
                host = "localhost"
                port = "27017"
                databaseName = "seasonal-calendar-test"
            }
        }
    }
    production {
        grails {
            mongo {
                host = "localhost"
                port = "27017"
                databaseName = "seasonal-calendar"
            }
        }
    }
}
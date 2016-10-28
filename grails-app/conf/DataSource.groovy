environments {
    development {
        grails {
            mongodb {
                host = "localhost"
                port = "27017"
                databaseName = "seasonal-calendar"
            }
        }
    }
    test {
        grails {
            mongodb {
                host = "localhost"
                port = "27017"
                databaseName = "seasonal-calendar-test"
            }
        }
    }
    production {
        grails {
            mongodb {
                host = "localhost"
                port = "27017"
                databaseName = "seasonal-calendar"
            }
        }
    }
}
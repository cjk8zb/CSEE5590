## HBase Commands
# General HBase shell commands

status 'simple'

version

# Tables Management commands

create 'locations', 'name', 'parent', 'child'

describe 'access_log'

# Data Manipulation commands

truncate 'locations'

scan 'access_log'

# HBase surgery tools

compact 'locations'

flush 'access_log'

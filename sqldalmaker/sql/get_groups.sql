select g.*,
(select count(*) from tasks where g_ID=g.g_ID) as tasks_count
from groups g

Narrative:
In order to learn JBehave
As a developer
I want to define sample story for timetable management system application concerning searching timetables for student

Scenario: empty search result if user is not registered for any course scheduled for given month

Given 3 course timetables for month
When student monthly timetables search is conducted
Then empty result is returned

Scenario: result containing allcourse timetables for target month if student registered for such course

Given 3 course timetables for month
And student is registered for given course
When student monthly timetables search is conducted
Then result with 3 timetables returned
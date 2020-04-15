# Providing AWS credentials

You'll need AWS credentials, which have [full JPT permissions].
The order of loading credentials is defined in [`AwsVus`] in the `prepareAws` method.

## AWS credentials for Atlassian engineering
If you are a member of an Atlassian engineering departament/team,
you can obtain AWS credentials via our internal tool - [cloudtoken].
You can run it in daemon mode: `cloudtoken -d`
and assume the `arn:aws:iam::695067801333:role/Jira-Server-Perf-Dev-Protection-Pipeline` role.
It already has the permissions set up.

[full JPT permissions]: https://bitbucket.org/atlassian/jira-performance-tests/src/master/aws-policy.json
[`AwsVus`]: ../src/test/kotlin/jces1209/AwsVus.kt
[cloudtoken]: http://go.atlassian.com/cloudtoken

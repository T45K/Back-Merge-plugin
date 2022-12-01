# Back Merge plugin

## Introduction
Have you ever found it annoying to merge main branch into your feature branch when the main branch is updated?
Or, have you ever been caught in a panic right before merging your feature branch into the main branch because you didn't notice an update on the main branch?

This plugin will solve such problems!

## Steps
1. You merge a feature branch into the main branch.
2. Bitbucket server notifies Jenkins of the event.
3. Jenkins starts a job containing build step of this plugin.
4. This plugin fetches all opened pull requests and create pull requests from the main branch to branches of those pull request.
5. What you should do is just merging them!

## Settings
1. Download the latest version of Back-Merge-plugin from [Release](https://github.com/T45K/Back-Merge-plugin/releases).
2. Deploy it to your Jenkins via `/pluginManager/advanced`.
3. Go to Jenkins `/configure`, and fill in Back Merge plugin settings.
4. Create new Freestyle job.
5. In its configure page, specify `Create back merge pull requests` build step.

## Configure
### Global configure
|Name|Description|
|:--:|:--|
|URL of Git repository hosting service|Usually like `https://your.bitbuckt.server.url`.|
|Basic auth credential|This values will be used for calling Bitbucket Server API. This will be the same as your usaname and password of Bitbucket server.|

### Job configure
|Name|Description|
|:--:|:--|
|Project name|`xxx` of `/projects/xxx/repos/yyy` in the case of Bitbucket Server.|
|Repository name|`yyy` of `/projects/xxx/repos/yyy` in the case of Bitbucket Server.|
|Base branch name|For example, `main`, `master`, `work`, etc.|

## Note
- This plugin is currently used for only Bitbucket Server.
- You need to set up trigger of your job by yourself. I recommend `Bitbucket-Server-Integration` plugin.

## LICENSE
Licensed under MIT, see LICENSE

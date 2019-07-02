# AnonyTweet

A social networking Android app where anonymity is the priority. Posting, reacting, commenting can be done like confessions via this app.


## Error logs
I am not able to get why `androidresource-checker` give the warnings stated below:

```cmd
> Task :app:compileCheckTypesJavaWithJavac
E:\Android Projects\AnonyTweet\app\src\main\java\com\ayush\anonytweet\myFavourites.java:143: warning: [binary.operation.not.allowed] CONDITIONAL_AND binary operation involve Resource operand.
                            if (user != null && user.getData_id() != null && user.getData_id().equals(favTweets.getTweetIds().get(i)))
                                                                          ^
E:\Android Projects\AnonyTweet\app\src\main\java\com\ayush\anonytweet\myFavourites.java:143: warning: [binary.operation.not.allowed] CONDITIONAL_AND binary operation involve Resource operand.
                            if (user != null && user.getData_id() != null && user.getData_id().equals(favTweets.getTweetIds().get(i)))
                                             ^
E:\Android Projects\AnonyTweet\app\src\main\java\com\ayush\anonytweet\myFavourites.java:159: warning: [binary.operation.not.allowed] CONDITIONAL_AND binary operation involve Resource operand.
                        if (list_user.size() != 0 && usersLiked != null && usersLiked.getTweetId() != null && list_user.get(temp).getData_id().equals(usersLiked.getTweetId())) {
                                                                                                           ^
E:\Android Projects\AnonyTweet\app\src\main\java\com\ayush\anonytweet\myFavourites.java:159: warning: [binary.operation.not.allowed] CONDITIONAL_AND binary operation involve Resource operand.
                        if (list_user.size() != 0 && usersLiked != null && usersLiked.getTweetId() != null && list_user.get(temp).getData_id().equals(usersLiked.getTweetId())) {
                                                                        ^
E:\Android Projects\AnonyTweet\app\src\main\java\com\ayush\anonytweet\MyTweets.java:132: warning: [binary.operation.not.allowed] CONDITIONAL_AND binary operation involve Resource operand.
                    if (user != null && user.getEmail() != null && FirebaseAuth.getInstance().getCurrentUser() != null && user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                                                ^
E:\Android Projects\AnonyTweet\app\src\main\java\com\ayush\anonytweet\MyTweets.java:132: warning: [binary.operation.not.allowed] CONDITIONAL_AND binary operation involve Resource operand.
                    if (user != null && user.getEmail() != null && FirebaseAuth.getInstance().getCurrentUser() != null && user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                     ^
Note: Some input files use or override a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
6 warnings
```
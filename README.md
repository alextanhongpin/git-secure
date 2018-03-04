# Git Secrets

## Installation

```bash
$ brew install git-secrets
```

## Setup

```bash
# Go to your repository
$ cd /path/to/repo

# Setup git secrets
$ git secrets --install
```

Output:

```bash
✓ Installed commit-msg hook to .git/hooks/commit-msg
✓ Installed pre-commit hook to .git/hooks/pre-commit
✓ Installed prepare-commit-msg hook to .git/hooks/prepare-commit-msg
```

## Adding AWS Provider

Installing this provider will check against your local `.aws/credentials` to ensure none of them are accidentally committed to the repository. Note that this does not work if you do not have your AWS Credentials stored on your device. To handle that, we will add custom regex in the next step.

```bash
$ git secrets --add-provider -- git secrets --aws-provider
```

## Adding Other Providers

This will add regex to match the *api keys* or *api secrets* from other providers such as Amazon, Bitly, Facebook, Flickr, Foursquare, LinkedIn, Twitter. It will also check against password (just plain regex).

```bash
git secrets --add 'password\s*=\s*.+'
git secrets --add  'AWS AKIA[0-9A-Z]{16}'
git secrets --add  '[0-9a-zA-Z/+]{20}'
git secrets --add  '[0-9a-zA-Z/+]{40}'
git secrets --add  '[0-9a-zA-Z_]{5,31} '
git secrets --add  'R_[0-9a-f]{32}'
git secrets --add  '[0-9]{13,17} '
git secrets --add  '[0-9a-f]{32}'
git secrets --add  '[0-9a-f]{32} '
git secrets --add  '[0-9a-f]{16}'
git secrets --add  '[0-9A-Z]{48} '
git secrets --add  '[0-9A-Z]{48}'
git secrets --add  '[0-9a-z]{12} '
git secrets --add  '[0-9a-zA-Z]{16}'
git secrets --add  '[0-9a-zA-Z]{18,25} '
git secrets --add  '[0-9a-zA-Z]{35,44}'
```

## Validate

To validate that this is running, you can either scan the commits:


```bash
$ git secrets --scan -r
```

Or just commit as usual:

```bash
$ git commit -S -am 'chore: commit password'
```

Output:

```
.env:1:aws_access_key_id = AKIAEXAMPLEFAKT30KEN
.env:2:aws_secret_access_key = AbcdEfg41jklMnop99rstuvwxy//4ZoP01mLOFabc
Amazon.java:1:String ACCESS_KEY_ID = "AKIA2E0A8F3B244C9986";
Amazon.java:2:String SECRET_KEY = "7CE556A3BC234CC1FF9E8A5C324C0BB70AA21B6D";
Amazon.java:4:AWSCredentials creds = new BasicAWSCredentials(ACCESS_KEY_ID, SECRET_KEY);
Amazon.java:5:AmazonSimpleDBClient client = new AmazonSimpleDBClient(creds);
Facebook.java:1:OAuthClientRequest request = OAuthClientRequest
Facebook.java:2:  .tokenProvider(OAuthProviderType.FACEBOOK)
Facebook.java:3:  .setGrantType(GrantType.AUTHORIZATION_CODE)
Facebook.java:4:  .setClientId("950513172001321")
Facebook.java:5:  .setClientSecret("3b2e464637e5159024254dd78aadb17a")
Facebook.java:6:  .setRedirectURI("http://localhost:8080/facebooklogin")
Facebook.java:7:  .setCode(code)
Facebook.java:8:  .buildQueryMessage();

[ERROR] Matched one or more prohibited patterns

Possible mitigations:
- Mark false positives as allowed using: git config --add secrets.allowed ...
- Mark false positives as allowed by adding regular expressions to .gitallowed at repository's root directory
- List your configured patterns: git config --get-all secrets.patterns
- List your configured allowed patterns: git config --get-all secrets.allowed
- List your configured allowed patterns in .gitallowed at repository's root directory
- Use --no-verify if this is a one-time false positive
```

## Explanation on Amazon Regex

- Search for *access key IDs*: `(?<![A-Z0-9])[A-Z0-9]{20}(?![A-Z0-9])`. In English, this regular expression says: Find me 20-character, uppercase, alphanumeric strings that don’t have any uppercase, alphanumeric characters immediately before or after.
- Search for *secret access keys*: `(?<![A-Za-z0-9/+=])[A-Za-z0-9/+=]{40}(?![A-Za-z0-9/+=])`. In English, this regular expression says: Find me 40-character, base-64 strings that don’t have any base 64 characters immediately before or after."

### Perl Regex Version of the Amazon Secret and Access Key

`cli` version that you can run on your terminal directly:

```bash
$ perl -nle 'print if m{(?<![A-Z0-9])[A-Z0-9]{20}(?![A-Z0-9])}' .env
$ perl -nle 'print if m{(?<![A-Za-z0-9\/+=])[A-Za-z0-9\/+=]{40}(?![A-Za-z0-9\/+=])}' .env
```

## Equivalent find
```bash
# For access key
$ find . | xargs perl -nle 'print if m{(?<![A-Z0-9])[A-Z0-9]{20}(?![A-Z0-9])}'

# For secret access key
$ find . | xargs perl -nle 'print if m{(?<![A-Za-z0-9\/+=])[A-Za-z0-9\/+=]{40}(?![A-Za-z0-9\/+=])}'

# Equivalent but with -exec
$ find . -exec perl -nle 'print if m{(?<![A-Z0-9])[A-Z0-9]{20}(?![A-Z0-9])}' {} \;

# To print out just the matches
$ find . -exec perl -nle 'print $& if m{(?<![A-Z0-9])[A-Z0-9]{20}(?![A-Z0-9])}' {} \;

<!--find . -not -path '*/\.*' | xargs -L1 bash -c 'if [ $(echo -n xargs ./access_key.sh $0 | wc -c) == 0 ]; then echo "empty"; else echo "$0" ; ./access_key.sh "$0"; fi'-->
```

## Other providers

Regex for matching other keys:

| Service Provider | Client ID | Secret Key |
| --               | --        | --         |
| Amazon  | AWS AKIA[0-9A-Z]{16} | [0-9a-zA-Z/+]{40} |
| Bitly | [0-9a-zA-Z_]{5,31} | R_[0-9a-f]{32}|
| Facebook | [0-9]{13,17} | [0-9a-f]{32}|
| Flickr | [0-9a-f]{32} | [0-9a-f]{16}|
| Foursquare | [0-9A-Z]{48} | [0-9A-Z]{48}|
| LinkedIn | [0-9a-z]{12} | [0-9a-zA-Z]{16}|
| Twitter | [0-9a-zA-Z]{18,25} | [0-9a-zA-Z]{35,44}|


## Other Tools

### TruffleHog

```bash
$ pip install truffleHog
$ truffleHog --regex --entropy=False https://github.com/dxa4481/truffleHog.git

# Example on this repo
$ truffleHog --regex --entropy=False https://github.com/alextanhongpin/git-secure.git
```

### Git Hound

Requires golang.

```bash
$ go get github.com/ezekg/git-hound
```

Usage: 

```bash
# Sniff changes since last commit
$ git hound sniff HEAD

# Sniff entire codebase
$ git hound sniff

# Sniff entire repo history
$ git log -p | git hound sniff
```


## Reference:

- https://people.eecs.berkeley.edu/~rohanpadhye/files/key_leaks-msr15.pdf



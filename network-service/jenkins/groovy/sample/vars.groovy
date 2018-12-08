properties([
	buildDiscarder(logRotator(numToKeepStr: '10')),
	disableConcurrentBuilds(),
	pipelineTriggers([
		cron('H H * * *'),
	]),
])

def vars = fileLoader.fromGit(
 	'nginx/vars.groovy',
 	'https://github.com/48763/jenkins-test-project.git', // repo
 	'master', // branch
 	null, // credentialsId
 	'master', // node/label
)

def repo = env.JOB_BASE_NAME
def repoMeta = vars.repoMeta(repo)

node {

    env.BASH_CACHE = env.WORKSPACE + '/bash-cache'

	env.BRANCH_BASE = repoMeta['branch-base']
	env.BRANCH_PUSH = repoMeta['branch-push']
	
    stage('Checkout') {
		sh 'mkdir -p "$BASH_CACHE"'

		// Pull repository and check directory
		checkout([
			$class: 'GitSCM',
			userRemoteConfigs: [[
				name: 'origin',
				url: repoMeta['url'],
				credentialsId: 'github-48763-bot',
			]],
			branches: [[name: '*/' + repoMeta['branch-push']]],
			extensions: [
				[
					$class: 'CleanCheckout',
				],
				[
					$class: 'RelativeTargetDirectory',
					relativeTargetDir: 'repo',
				],
			],
			doGenerateSubmoduleConfigurations: false,
			submoduleCfg: [],
		])

		sh '''
			cd repo
			git config user.name '48763'
			git config user.email 'future.starshine@gmail.com'
		'''

		// Check main branch
		if (repoMeta['branch-base'] != repoMeta['branch-push']) {
			sshagent(['github-48763-bot']) {
				sh '''
					git -C repo pull --rebase origin "$BRANCH_BASE"
				'''
			}
		}

	}

	// Setting work directory.
	dir('repo/'+repo) {

		stage('update.sh') {
			
			retry(3) {
				sh './update.sh "$JOB_BASE_NAME"'
			}
		}

		stage('Diff') {
			sh '''
				git status
				git diff
			'''
		}

		stage('Commit') {

			sh '#!/bin/bash' + '''
				VERSION=$(grep CUR_VERSION operations/.env.info | sed 's/CUR_VERSION=//g')
				git add "operations/.env.info"* || true
				git commit -m "$JOB_BASE_NAME update to $VERSION" || true
			'''
		}

		stage('Log') {
			sh '''
				git status
			'''
		}

		def numCommits = sh(
				returnStdout: true,
				script: 'git rev-list --count "origin/$BRANCH_BASE...HEAD"',
		).trim().toInteger()
		def hasChanges = (numCommits > 0)

		if (hasChanges) {

			stage('Build') {
				sh '#!/bin/bash' + """
					sh build.sh
					rm build.sh
				"""
			}

			stage('Test') {
				echo 'Container'
			}

			stage('Push') {
				sshagent(['github-48763-bot']) {
					sh 'git push $([ "$BRANCH_BASE" = "$BRANCH_PUSH" ] || echo --force) origin "HEAD:$BRANCH_PUSH"'
				}

				withDockerRegistry([credentialsId: "harbor-admin", url:"https://lab.yukifans.com"]) {
					sh '''
						VERSION=$(grep CUR_VERSION operations/.env.info | sed 's/CUR_VERSION=//g')
						docker push lab.yukifans.com/jenkins/$JOB_BASE_NAME:$VERSION
					'''
				}
			}

			stage('Run') {
				sshagent(['remote-lab']) {
					sh './run.sh'
				}
			}

		} else {
				echo("No changes in ${repo}!  Skipping.")
		}

	}

}
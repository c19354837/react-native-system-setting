require 'json'
pjson = JSON.parse(File.read('package.json'))

Pod::Spec.new do |s|

  s.name            = "RCTSystemSetting"
  s.version         = pjson["version"]
  s.homepage        = pjson["homepage"]
  s.summary         = pjson["description"]
  s.license         = pjson["license"]
  s.author          = { "Ninty" => "c19354837@hotmail.com" }
  
  s.ios.deployment_target = '7.0'

  s.source          = { :git => "https://github.com/c19354837/react-native-system-setting", :tag => "v#{s.version}" }
  s.source_files    = 'ios/*.{h,m}'
  s.preserve_paths  = "**/*.js"

  s.dependency 'React'
end
